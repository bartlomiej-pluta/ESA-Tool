package com.bartek.esa;

import com.bartek.esa.cli.parser.CliArgsParser;
import com.bartek.esa.di.DaggerDependencyInjector;

import javax.inject.Inject;

public class EsaMain {
    private final CliArgsParser cliArgsParser;

    @Inject
    EsaMain(CliArgsParser cliArgsParser) {
        this.cliArgsParser = cliArgsParser;
    }

    private void run(String[] args) {
        // cliArgsParser.parse(...)
    }

    public static void main(String[] args) {
        DaggerDependencyInjector.create()
                .esa()
                .run(args);
    }
}
