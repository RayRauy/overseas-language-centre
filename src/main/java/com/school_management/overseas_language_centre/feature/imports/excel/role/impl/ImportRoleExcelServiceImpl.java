package com.school_management.overseas_language_centre.feature.imports.excel.role.impl;

import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.feature.core.role.dto.request.RoleRequest;
import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleImportResult;
import com.school_management.overseas_language_centre.feature.core.role.mapper.RoleMapper;
import com.school_management.overseas_language_centre.feature.core.role.normalizer.RoleRequestNormalizer;
import com.school_management.overseas_language_centre.feature.core.role.repository.RoleRepository;
import com.school_management.overseas_language_centre.feature.imports.excel.role.ImportRoleExcelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImportRoleExcelServiceImpl implements ImportRoleExcelService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RoleRequestNormalizer roleRequestNormalizer;
    private final DataFormatter dataFormatter = new DataFormatter();

    private String cellString(Cell cell) {
        if (cell == null) {
            return null;
        }

        String value = dataFormatter.formatCellValue(cell);
        return (value == null || value.isBlank()) ? null : value.trim();
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
}
