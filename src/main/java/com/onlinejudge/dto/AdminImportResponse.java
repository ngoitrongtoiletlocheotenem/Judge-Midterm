package com.onlinejudge.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminImportResponse {
    private int problemsImported;
    private int testCasesImported;
    private boolean clearedExisting;
}