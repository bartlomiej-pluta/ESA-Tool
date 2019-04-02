package com.bartek.esa;

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

    @Inject
    EsaMain(CliArgsParser cliArgsParser, MethodDispatcher methodDispatcher) {
        this.cliArgsParser = cliArgsParser;
        this.methodDispatcher = methodDispatcher;
    }

    private void run(String[] args) {
        DispatcherActions dispatcherActions = DispatcherActions.builder()
                .sourceAnalysis((source, plugins, excludes) -> null)
                .apkAudit((source, plugins, excludes) -> null)
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
