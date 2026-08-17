package com.school_management.overseas_language_centre.feature.core.role.service.impl;

import com.school_management.overseas_language_centre.feature.core.role.dto.filter.RoleFilter;
import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleImportResult;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleResponse;
import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.exceptions.ResourceNotFoundException;
import com.school_management.overseas_language_centre.feature.core.role.mapper.RoleMapper;
import com.school_management.overseas_language_centre.feature.core.role.normalizer.RoleRequestNormalizer;
import com.school_management.overseas_language_centre.feature.core.role.repository.RoleRepository;
import com.school_management.overseas_language_centre.feature.core.role.service.RoleService;
import com.school_management.overseas_language_centre.feature.core.role.specifications.RoleSpecification;
import com.school_management.overseas_language_centre.feature.core.role.validator.RoleValidator;
import com.school_management.overseas_language_centre.feature.core.role.validator.RoleValidator_usingGlobalMethods;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RoleRequestNormalizer roleRequestNormalizer;
    private final RoleValidator roleValidator;
    private final RoleValidator_usingGlobalMethods roleValidatorUsingGlobalMethods;
    private final DataFormatter dataFormatter = new DataFormatter();

    @Override
    public RoleResponse getById(Long id) {
        return roleRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role With Id " + id + " Not Found")
                );
    }

    @Override
    public List<RoleResponse> getAll(RoleFilter filter) {
        Specification<Role> spec = RoleSpecification.build(filter);
        Sort sort = RoleSpecification.sort(filter);
        return roleRepository.findAll(spec, sort)
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Override
    public Page<RoleResponse> getAllPagination(RoleFilter filter) {
        Specification<Role> spec = RoleSpecification.build(filter);
        Pageable pageable = RoleSpecification.pageable(filter);
        Page<Role> roles = roleRepository.findAll(spec, pageable);
        return roles.map(roleMapper::toResponse);
    }

    @Transactional
    @Override
    public RoleImportResult importFromXlsx(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("XLSX file is required and must not be empty");
        }
        int total = 0;
        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();
        Set<String> seenInFile = new HashSet<>();

        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) {
                throw new IllegalArgumentException("XLSX is empty (No Header Rows)");
            }

            int nameCol = -1;
            int descCol = -1;

            for (Cell cell : header) {
                String h = cellString(cell);
                if (h == null) {
                    continue;
                }

                if (h.equalsIgnoreCase("name")) {
                    nameCol = cell.getColumnIndex();
                } else if (h.equalsIgnoreCase("description")) {
                    descCol = cell.getColumnIndex();
                }
            }

            if (nameCol == -1) {
                throw new IllegalArgumentException("XLSX must contain a 'name' column: ");
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                total++;
                int line = r + 1;

                RoleRequest request = new RoleRequest();
                request.setName(cellString(row.getCell(nameCol)));
                request.setDescription(descCol >= 0 ? cellString(row.getCell(descCol)) : null);

                roleRequestNormalizer.normalize(request);
                if (request.getName() == null) {
                    skipped++;
                    errors.add("Row " + line + ": name is blank");
                    continue;
                }

                if (!seenInFile.add(request.getName())) {
                    skipped++;
                    errors.add("Row " + line + ": '" + request.getName() + "' is duplicated");
                    continue;
                }

                if (roleRepository.existsByNameIgnoreCase(request.getName())) {
                    skipped++;
                    errors.add("Row " + line + ": '" + request.getName() + "' already exists");
                    continue;
                }

                Role entity = roleMapper.toEntity(request);
                roleRepository.save(entity);
                imported++;
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read XLSX file: " + e.getMessage());
        }

        return RoleImportResult.builder()
                .totalRows(total)
                .imported(imported)
                .skipped(skipped)
                .errors(errors).build();
    }

    @Transactional
    @Override
    public byte[] exportToXlsx() {

        List<Role> roles = roleRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Roles");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            headerStyle.setFont(headerFont);

            // Header Row
            Row header = sheet.createRow(0);

            Cell nameHeader = header.createCell(0);
            nameHeader.setCellValue("name");
            nameHeader.setCellStyle(headerStyle);

            Cell descriptionHeader = header.createCell(1);
            descriptionHeader.setCellValue("description");
            descriptionHeader.setCellStyle(headerStyle);

            // Data Rows
            int rowIndex = 1;

            for (Role role : roles) {

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(
                        role.getName() != null
                                ? role.getName() : ""
                );

                row.createCell(1).setCellValue(
                        role.getDescription() != null
                                ? role.getDescription() : ""
                );
            }

            // Freeze Header Row
            sheet.createFreezePane(0, 1);

            // Auto Filter
            if (!roles.isEmpty()) {
                sheet.setAutoFilter(
                        new CellRangeAddress(0, roles.size(),
                                0,
                                1
                        )
                );
            }

            // Auto Size Columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // Add a little extra width
            sheet.setColumnWidth(
                    0,
                    Math.min(sheet.getColumnWidth(0) + 1000, 255 * 256)
            );

            sheet.setColumnWidth(
                    1,
                    Math.min(sheet.getColumnWidth(1) + 1000, 255 * 256)
            );

            // Write Workbook
            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Failed to generate XLSX file: " + e.getMessage(),
                    e
            );
        }
    }

    private String cellString(Cell cell) {
        if (cell == null) {
            return null;
        }

        String value = dataFormatter.formatCellValue(cell);
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    @Override
    public RoleResponse create(RoleRequest request) {
        roleRequestNormalizer.normalize(request);

        roleValidatorUsingGlobalMethods.validateCreate(request);

        Role entity = roleMapper.toEntity(request);
        Role save = roleRepository.save(entity);
        return roleMapper.toResponse(save);
    }

    @Override
    public RoleResponse updateById(Long id, RoleRequest request) {
        roleRequestNormalizer.normalize(request);

        Role entity = roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Role Not Found with id " + id)
        );

        roleValidator.validateUpdate(id, request);

        roleMapper.updateEntity(entity, request);
        Role response = roleRepository.save(entity);
        return roleMapper.toResponse(response);
    }

    @Override
    public void deleteById(Long id) {
        Role entity = roleRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Role Not Found with id " + id)
                );
        roleRepository.delete(entity);
    }
}
