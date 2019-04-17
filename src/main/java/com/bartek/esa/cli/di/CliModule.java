package com.bartek.esa.cli.di;

import com.bartek.esa.cli.parser.CliArgsParser;
import com.bartek.esa.cli.printer.PluginPrinter;
import com.bartek.esa.core.archetype.Plugin;
import dagger.Module;
import dagger.Provides;

import java.util.Set;

@Module
public class CliModule {

    @Provides
    public CliArgsParser cliArgsParser(PluginPrinter pluginPrinter) {
        return new CliArgsParser(pluginPrinter);
    }

    @Provides
    public PluginPrinter pluginPrinter(Set<Plugin> plugins) {
        return new PluginPrinter(plugins);
    }
}
