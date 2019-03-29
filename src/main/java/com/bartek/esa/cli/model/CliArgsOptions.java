package com.bartek.esa.cli.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CliArgsOptions {
    private String sourceAnalysisDirectory;
    private String apkAuditFile;

    public boolean isSourceAnalysis() {
        return sourceAnalysisDirectory != null;
    }

    public boolean isApkAudit() {
        return apkAuditFile != null;
    }
}
