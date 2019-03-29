package com.bartek.esa.cli.di;

import com.bartek.esa.cli.parser.CliArgsParser;
import dagger.Module;
import dagger.Provides;

@Module
public class CliModule {

    @Provides
    CliArgsParser cliArgsParser() {
        return new CliArgsParser();
    }
}
