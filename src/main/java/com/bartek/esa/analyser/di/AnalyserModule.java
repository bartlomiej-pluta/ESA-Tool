package com.bartek.esa.analyser.di;

import com.bartek.esa.analyser.source.SourceAnalyser;
import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.file.provider.FileProvider;
import dagger.Module;
import dagger.Provides;

import java.util.Set;

@Module
public class AnalyserModule {

    @Provides
    public SourceAnalyser sourceAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider) {
        return new SourceAnalyser(pluginExecutor, plugins, fileProvider);
    }
}
