package com.bartek.esa.analyser.di;

import com.bartek.esa.analyser.apk.ApkAnalyser;
import com.bartek.esa.analyser.source.SourceAnalyser;
import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.decompiler.decompiler.Decompiler;
import com.bartek.esa.file.cleaner.FileCleaner;
import com.bartek.esa.file.matcher.GlobMatcher;
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

   @Provides
    public ApkAnalyser apkAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, Decompiler decompiler, FileCleaner fileCleaner, GlobMatcher globMatcher) {
        return new ApkAnalyser(pluginExecutor, plugins, fileProvider, decompiler, fileCleaner, globMatcher);
    }
}
