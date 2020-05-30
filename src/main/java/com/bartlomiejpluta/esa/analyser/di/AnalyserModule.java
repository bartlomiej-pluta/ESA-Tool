package com.bartlomiejpluta.esa.analyser.di;

import com.bartlomiejpluta.esa.analyser.apk.ApkAnalyser;
import com.bartlomiejpluta.esa.analyser.source.SourceAnalyser;
import com.bartlomiejpluta.esa.context.constructor.ContextConstructor;
import com.bartlomiejpluta.esa.core.archetype.Plugin;
import com.bartlomiejpluta.esa.core.executor.PluginExecutor;
import com.bartlomiejpluta.esa.decompiler.archetype.Decompiler;
import com.bartlomiejpluta.esa.file.cleaner.FileCleaner;
import com.bartlomiejpluta.esa.file.matcher.GlobMatcher;
import com.bartlomiejpluta.esa.file.provider.FileProvider;
import dagger.Module;
import dagger.Provides;

import java.util.Set;

@Module
public class AnalyserModule {

    @Provides
    public SourceAnalyser sourceAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, ContextConstructor contextConstructor) {
        return new SourceAnalyser(pluginExecutor, plugins, fileProvider, contextConstructor);
    }

   @Provides
    public ApkAnalyser apkAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, Decompiler decompiler, FileCleaner fileCleaner, GlobMatcher globMatcher, ContextConstructor contextConstructor) {
        return new ApkAnalyser(pluginExecutor, plugins, fileProvider, decompiler, fileCleaner, globMatcher, contextConstructor);
    }
}
