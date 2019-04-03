package com.bartek.esa;

import com.bartek.esa.analyser.apk.ApkAnalyser;
import com.bartek.esa.analyser.source.SourceAnalyser;
import com.bartek.esa.cli.model.CliArgsOptions;
import com.bartek.esa.cli.parser.CliArgsParser;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.di.DaggerDependencyInjector;
import com.bartek.esa.dispatcher.dispatcher.MethodDispatcher;
import com.bartek.esa.dispatcher.model.DispatcherActions;

import javax.inject.Inject;
import java.util.List;

public class EsaMain {
    private final CliArgsParser cliArgsParser;
    private final MethodDispatcher methodDispatcher;
    private final SourceAnalyser sourceAnalyser;
    private final ApkAnalyser apkAnalyser;

    @Inject
    EsaMain(CliArgsParser cliArgsParser, MethodDispatcher methodDispatcher, SourceAnalyser sourceAnalyser, ApkAnalyser apkAnalyser) {
        this.cliArgsParser = cliArgsParser;
        this.methodDispatcher = methodDispatcher;
        this.sourceAnalyser = sourceAnalyser;
        this.apkAnalyser = apkAnalyser;
    }

    private void run(String[] args) {
        DispatcherActions dispatcherActions = DispatcherActions.builder()
                .sourceAnalysis(sourceAnalyser::analyse)
                .apkAudit(apkAnalyser::analyse)
                .build();

        CliArgsOptions options = cliArgsParser.parse(args);

        List<Issue> issues = methodDispatcher.dispatchMethod(options, dispatcherActions);
    }

    public static void main(String[] args) {
        DaggerDependencyInjector.create()
                .esa()
                .run(args);
    }
}
