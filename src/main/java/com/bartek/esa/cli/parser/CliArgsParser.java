package com.bartek.esa.cli.parser;

import com.bartek.esa.cli.model.CliArgsOptions;
import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;
import org.apache.commons.cli.*;

import javax.inject.Inject;

public class CliArgsParser {
    private static final String SRC_OPT = "src";
    private static final String APK_OPT = "apk";

    @Inject
    public CliArgsParser() {}

    public CliArgsOptions parse(String[] args) {
        CommandLine command = Try.of(() -> new DefaultParser().parse(prepareOptions(), args))
                .getOrElseThrow(EsaException::new);

        return CliArgsOptions.builder()
                .sourceAnalysisDirectory(command.hasOption(SRC_OPT) ? command.getOptionValue(SRC_OPT) : null)
                .apkAuditFile(command.hasOption(APK_OPT) ? command.getOptionValue(APK_OPT) : null)
                .build();
    }

    private Options prepareOptions() {
        Options options = new Options();
        options.addOption(source());
        options.addOption(apk());
        return options;
    }

    private Option source() {
        return Option.builder()
                .longOpt(SRC_OPT)
                .hasArg()
                .desc("perform analysis for Java code and XML resources")
                .build();
    }

    private Option apk() {
        return Option.builder()
                .longOpt(APK_OPT)
                .hasArg()
                .desc("perform audit for compiled APK file")
                .build();
    }
}
