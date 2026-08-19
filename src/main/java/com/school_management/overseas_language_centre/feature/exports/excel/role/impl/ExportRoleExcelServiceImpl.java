package com.school_management.overseas_language_centre.feature.exports.excel.role.impl;

import com.school_management.overseas_language_centre.entity.Role;
import com.school_management.overseas_language_centre.feature.core.role.repository.RoleRepository;
import com.school_management.overseas_language_centre.feature.exports.excel.role.ExportRoleExcelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportRoleExcelServiceImpl implements ExportRoleExcelService {

    private final RoleRepository roleRepository;

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
}
