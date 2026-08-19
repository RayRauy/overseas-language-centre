package com.school_management.overseas_language_centre.feature.exports.excel.role;

import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface ExportRoleExcelService {
    byte[] exportToXlsx();

}
