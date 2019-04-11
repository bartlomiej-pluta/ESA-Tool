package com.bartek.esa.cli.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

import static java.util.Collections.emptySet;

@Data
@Builder
public class CliArgsOptions {
    private String sourceAnalysisDirectory;
    private String apkAuditFile;
    private Set<String> excludes;
    private Set<String> plugins;
    private boolean color;
    private Set<String> severities;
    private boolean debug;

    public boolean isSourceAnalysis() {
        return sourceAnalysisDirectory != null;
    }

    public boolean isApkAudit() {
        return apkAuditFile != null;
    }

    public static CliArgsOptions empty() {
        return CliArgsOptions.builder()
                .excludes(emptySet())
                .plugins(emptySet())
                .build();
    }
}
