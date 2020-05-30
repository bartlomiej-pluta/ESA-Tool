package com.bartlomiejpluta.esa.cli.di;

import com.bartlomiejpluta.esa.cli.parser.CliArgsParser;
import com.bartlomiejpluta.esa.cli.printer.PluginPrinter;
import com.bartlomiejpluta.esa.core.archetype.Plugin;
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
