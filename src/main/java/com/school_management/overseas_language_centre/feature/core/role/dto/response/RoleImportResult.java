package com.school_management.overseas_language_centre.feature.core.role.dto.response;

import java.util.List;

public class RoleImportResult {
    private int totalRows;
    private int imported;
    private int skipped;
    private List<String> errors;
}
