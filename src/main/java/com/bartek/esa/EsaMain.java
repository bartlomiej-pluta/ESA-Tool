package com.bartek.esa;

import com.bartek.esa.analyser.apk.ApkAnalyser;
import com.bartek.esa.analyser.source.SourceAnalyser;
import com.bartek.esa.cli.model.CliArgsOptions;
import com.bartek.esa.cli.parser.CliArgsParser;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.di.DaggerDependencyInjector;
import com.bartek.esa.dispatcher.dispatcher.MethodDispatcher;
import com.bartek.esa.dispatcher.model.DispatcherActions;
import com.bartek.esa.formatter.provider.FormatterProvider;

import javax.inject.Inject;
import java.util.List;

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
        List<Issue> issues = methodDispatcher.dispatchMethod(options, dispatcherActions);
        formatterProvider.provide(options).format(issues);

        exitWithErrorIfAnyIssueIsAnError(issues);
    }

    private void exitWithErrorIfAnyIssueIsAnError(List<Issue> issues) {
        if(issues.stream().anyMatch(i -> i.getSeverity() == Severity.ERROR)) {
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        DaggerDependencyInjector.create()
                .esa()
                .run(args);
    }
}
