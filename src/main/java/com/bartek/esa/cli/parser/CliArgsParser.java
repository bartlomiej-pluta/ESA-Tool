package com.bartek.esa.cli.parser;

import com.bartek.esa.cli.model.CliArgsOptions;
import org.apache.commons.cli.*;

import javax.inject.Inject;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

public class CliArgsParser {
    private static final String SRC_OPT = "src";
    private static final String APK_OPT = "apk";
    private static final String EXCLUDE_OPT = "exclude";
    private static final String HELP_OPT = "help";
    private static final String PLUGINS_OPT = "plugins";

    @Inject
    public CliArgsParser() {}

    public CliArgsOptions parse(String[] args) {
        try {
            return tryToParse(args);
        } catch (ParseException e) {
            printHelp();
            return CliArgsOptions.empty();
        }
    }

    private CliArgsOptions tryToParse(String[] args) throws ParseException {
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
                .plugins(command.hasOption(PLUGINS_OPT) ? new HashSet<>(asList(command.getOptionValues(PLUGINS_OPT))) : emptySet())
                .excludes(command.hasOption(EXCLUDE_OPT) ? new HashSet<>(asList(command.getOptionValues(EXCLUDE_OPT))) : emptySet())
                .build();
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
        options.addOption(plugins());
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
                .desc("print this help")
                .build();
    }

    private Option plugins() {
        return Option.builder()
                .longOpt(PLUGINS_OPT)
                .argName("CODES")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("use only selected security checks for audit/analysis")
                .build();
    }
}
