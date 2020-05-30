package com.bartlomiejpluta.esa.cli.model.object;

import com.bartlomiejpluta.esa.cli.model.enumeration.OutputType;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Set;

import static java.util.Collections.emptySet;

@Data
@Builder
public class CliArgsOptions {
    private String sourceAnalysisDirectory;
    private String apkAuditFile;
    private Set<String> excludes;
    private Set<String> plugins;
    private OutputType outputType;
    private Set<String> severities;
    private boolean debug;
    private File out;
    private boolean strictMode;

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
                .severities(emptySet())
                .outputType(OutputType.DEFAULT)
                .build();
    }
}
