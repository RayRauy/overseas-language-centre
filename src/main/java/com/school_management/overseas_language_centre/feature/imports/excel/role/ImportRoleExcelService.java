package com.school_management.overseas_language_centre.feature.imports.excel.role;

import com.school_management.overseas_language_centre.feature.core.role.dto.response.RoleImportResult;
import org.springframework.web.multipart.MultipartFile;

public interface ImportRoleExcelService {
    RoleImportResult importFromXlsx(MultipartFile file);
}
