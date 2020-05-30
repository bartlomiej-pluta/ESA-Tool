package com.bartlomiejpluta.esa;

import com.bartlomiejpluta.esa.analyser.apk.ApkAnalyser;
import com.bartlomiejpluta.esa.analyser.source.SourceAnalyser;
import com.bartlomiejpluta.esa.cli.model.object.CliArgsOptions;
import com.bartlomiejpluta.esa.cli.parser.CliArgsParser;
import com.bartlomiejpluta.esa.core.model.object.Issue;
import com.bartlomiejpluta.esa.di.DaggerDependencyInjector;
import com.bartlomiejpluta.esa.dispatcher.dispatcher.MethodDispatcher;
import com.bartlomiejpluta.esa.dispatcher.model.DispatcherActions;
import com.bartlomiejpluta.esa.error.EsaException;
import com.bartlomiejpluta.esa.formatter.archetype.Formatter;
import com.bartlomiejpluta.esa.formatter.provider.FormatterProvider;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EsaMain {
    private final CliArgsParser cliArgsParser;
    private final MethodDispatcher methodDispatcher;
    private final SourceAnalyser sourceAnalyser;
    private final ApkAnalyser apkAnalyser;
    private final FormatterProvider formatterProvider;

    @Inject
    EsaMain(CliArgsParser cliArgsParser, MethodDispatcher methodDispatcher, SourceAnalyser sourceAnalyser, ApkAnalyser apkAnalyser, FormatterProvider formatterProvider) {
        this.cliArgsParser = cliArgsParser;
        this.methodDispatcher = methodDispatcher;
        this.sourceAnalyser = sourceAnalyser;
        this.apkAnalyser = apkAnalyser;
        this.formatterProvider = formatterProvider;
    }

    private void run(String[] args) {
        DispatcherActions dispatcherActions = DispatcherActions.builder()
                .sourceAnalysis(sourceAnalyser::analyse)
                .apkAudit(apkAnalyser::analyse)
                .build();

        CliArgsOptions options = cliArgsParser.parse(args);
        Set<Issue> issues = methodDispatcher.dispatchMethod(options, dispatcherActions);
        Set<Issue> filteredIssues = filterIssuesBySeverity(options, issues);
        Formatter formatter = formatterProvider.provide(options);
        formatter.beforeFormat();
        String output = formatter.format(filteredIssues);
        displayOutputOrSaveToFile(options, output);
        formatter.afterFormat();

        if(options.isStrictMode()) {
            exitWithErrorIfAnyIssueIsAnError(filteredIssues);
        }
    }

    private Set<Issue> filterIssuesBySeverity(CliArgsOptions options, Set<Issue> issues) {
        return issues.stream()
                .filter(issue -> options.getSeverities().contains(issue.getSeverity().name()))
                .collect(Collectors.toSet());
    }

    private void displayOutputOrSaveToFile(CliArgsOptions options, String output) {
        Optional.ofNullable(options.getOut())
                .map(this::getWriter)
                .ifPresentOrElse(writeString(output), () -> System.out.println(output));
    }

    private BufferedWriter getWriter(File file) {
        return Try.of(() -> new BufferedWriter(new FileWriter(file)))
                .getOrElseThrow(EsaException::new);
    }

    private Consumer<BufferedWriter> writeString(String string) {
        return writer -> Try.run(() -> {
            writer.write(string);
            writer.close();
        }).getOrElseThrow(EsaException::new);
    }

    private void exitWithErrorIfAnyIssueIsAnError(Set<Issue> issues) {
        if(issues.stream().anyMatch(i -> i.getSeverity().isExitWithError())) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        DaggerDependencyInjector.create()
                .esa()
                .run(args);
    }
}
