package com.bartek.esa;

import com.bartek.esa.cli.model.CliArgsOptions;
import com.bartek.esa.dispatcher.dispatcher.MethodDispatcher;
import com.bartek.esa.dispatcher.model.DispatcherActions;
import com.bartek.esa.cli.parser.CliArgsParser;
import com.bartek.esa.di.DaggerDependencyInjector;

import javax.inject.Inject;

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
                .sourceAnalysis(source -> {})
                .apkAudit(source -> {})
                .build();

        CliArgsOptions options = cliArgsParser.parse(args);

        methodDispatcher.dispatchMethod(options, dispatcherActions);
    }

    public static void main(String[] args) {
        DaggerDependencyInjector.create()
                .esa()
                .run(args);
    }
}
