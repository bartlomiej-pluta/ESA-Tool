package com.bartek.esa.cli.model;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class CliArgsOptions {
    private String sourceAnalysisDirectory;
    private String apkAuditFile;
    private List<String> excludes;

    public boolean isSourceAnalysis() {
        return sourceAnalysisDirectory != null;
    }

    public boolean isApkAudit() {
        return apkAuditFile != null;
    }

    public static CliArgsOptions empty() {
        return CliArgsOptions.builder()
                .sourceAnalysisDirectory(null)
                .apkAuditFile(null)
                .excludes(Collections.emptyList())
                .build();
    }
}
