package com.bartek.esa.cli.parser;

import com.bartek.esa.cli.model.CliArgsOptions;
import org.apache.commons.cli.*;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class CliArgsParser {
    private static final String SRC_OPT = "src";
    private static final String APK_OPT = "apk";
    private static final String EXCLUDE_OPT = "exclude";
    private static final String HELP_OPT = "help";

    @Inject
    public CliArgsParser() {}

    public CliArgsOptions parse(String[] args) {
        try {
            CommandLine command = new DefaultParser().parse(prepareOptions(), args);

            if (command.hasOption(HELP_OPT)) {
                printHelp();
                return CliArgsOptions.empty();
            }

            boolean isNeitherAuditNorAnalysis = !command.hasOption(SRC_OPT) && !command.hasOption(APK_OPT);
            if (isNeitherAuditNorAnalysis) {
                printHelp();
                return CliArgsOptions.empty();
            }

            return CliArgsOptions.builder()
                    .sourceAnalysisDirectory(command.hasOption(SRC_OPT) ? command.getOptionValue(SRC_OPT) : null)
                    .apkAuditFile(command.hasOption(APK_OPT) ? command.getOptionValue(APK_OPT) : null)
                    .excludes(command.hasOption(EXCLUDE_OPT) ? asList(command.getOptionValues(EXCLUDE_OPT)) : emptyList())
                    .build();
        } catch (ParseException e) {
            printHelp();
            return CliArgsOptions.empty();
        }
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("esa", prepareOptions(), true);
    }

    private Options prepareOptions() {
        Options options = new Options();
        options.addOption(source());
        options.addOption(apk());
        options.addOption(exclude());
        options.addOption(help());
        return options;
    }

    private Option source() {
        return Option.builder()
                .longOpt(SRC_OPT)
                .argName("SOURCE_PATH")
                .hasArg()
                .desc("perform analysis for Java code and XML resources")
                .build();
    }

    private Option apk() {
        return Option.builder()
                .longOpt(APK_OPT)
                .argName("APK_PATH")
                .hasArg()
                .desc("perform audit for compiled APK file")
                .build();
    }

    private Option exclude() {
        return Option.builder()
                .longOpt(EXCLUDE_OPT)
                .argName("CODES")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("exclude particular security checks from audit/analysis")
                .build();
    }
    
    private Option help() {
        return Option.builder()
                .longOpt(HELP_OPT)
                .argName("h")
                .desc("print this help")
                .build();
    }
}
