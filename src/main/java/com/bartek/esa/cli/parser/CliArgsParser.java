package com.bartek.esa.cli.parser;

import com.bartek.esa.cli.model.enumeration.OutputType;
import com.bartek.esa.cli.model.object.CliArgsOptions;
import com.bartek.esa.cli.printer.PluginPrinter;
import com.bartek.esa.core.model.enumeration.Severity;
import io.vavr.control.Try;
import org.apache.commons.cli.*;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;

public class CliArgsParser {
    private static final String SRC_OPT = "src";
    private static final String APK_OPT = "apk";
    private static final String EXCLUDE_OPT = "exclude";
    private static final String HELP_OPT = "help";
    private static final String PLUGINS_OPT = "plugins";
    private static final String FORMAT_OPT = "format";
    private static final String OUT_OPT = "out";
    private static final String SEVERITIES_OPT = "severities";
    private static final String DEBUG_OPT = "debug";
    private static final String LIST_PLUGINS_OPT = "list-plugins";
    private static final String STRICT_OPT = "strict";

    private final PluginPrinter pluginPrinter;

    @Inject
    public CliArgsParser(PluginPrinter pluginPrinter) {
        this.pluginPrinter = pluginPrinter;
    }

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

        if (command.hasOption(LIST_PLUGINS_OPT)) {
            pluginPrinter.printPlugins();

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
                .outputType(getOutputTypeForOptions(command))
                .severities(command.hasOption(SEVERITIES_OPT) ? new HashSet<>(asList((command.getOptionValues(SEVERITIES_OPT)))) : stream(Severity.values()).map(Severity::name).collect(Collectors.toSet()))
                .debug(command.hasOption(DEBUG_OPT))
                .out(command.hasOption(OUT_OPT) ? new File(command.getOptionValue(OUT_OPT)) : null)
                .strictMode(command.hasOption(STRICT_OPT))
                .build();
    }

    private OutputType getOutputTypeForOptions(CommandLine command) {
        if(command.hasOption(FORMAT_OPT)) {
            return Try.of(() -> OutputType.valueOf(command.getOptionValue(FORMAT_OPT).toUpperCase()))
                    .getOrElse(OutputType.DEFAULT);
        }

        return OutputType.DEFAULT;
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
        options.addOption(format());
        options.addOption(severities());
        options.addOption(debug());
        options.addOption(listPlugins());
        options.addOption(out());
        options.addOption(strict());
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

    private Option format() {
        return Option.builder()
                .longOpt(FORMAT_OPT)
                .argName("OUTPUT_TYPE")
                .numberOfArgs(1)
                .desc("select format format (available: default, color, json)")
                .build();
    }

    private Option severities() {
        String severities = stream(Severity.values()).map(Severity::name).map(String::toUpperCase).collect(Collectors.joining(", "));
        return Option.builder()
                .longOpt(SEVERITIES_OPT)
                .argName("SEVERITY")
                .numberOfArgs(Option.UNLIMITED_VALUES)
                .desc("filter format to selected severities(available: " + severities + ")")
                .build();
    }

    private Option debug() {
        return Option.builder()
                .longOpt(DEBUG_OPT)
                .desc("enable debug mode")
                .build();
    }

    private Option listPlugins() {
        return Option.builder()
                .longOpt(LIST_PLUGINS_OPT)
                .desc("list available plugins")
                .build();
    }

    private Option out() {
        return Option.builder()
                .longOpt(OUT_OPT)
                .argName("PATH")
                .numberOfArgs(1)
                .desc("optional output of analysis - recommended to use with non-text output format")
                .build();
    }

    private Option strict() {
        return Option.builder()
                .longOpt(STRICT_OPT)
                .desc("enable strict mode - return code depends on analysis result (recommended to use in batch mode)")
                .build();
    }
}
