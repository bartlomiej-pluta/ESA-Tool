package com.bartlomiejpluta.esa.analyser.apk;

import com.bartlomiejpluta.esa.analyser.core.Analyser;
import com.bartlomiejpluta.esa.context.constructor.ContextConstructor;
import com.bartlomiejpluta.esa.core.archetype.Plugin;
import com.bartlomiejpluta.esa.core.executor.PluginExecutor;
import com.bartlomiejpluta.esa.decompiler.archetype.Decompiler;
import com.bartlomiejpluta.esa.error.EsaException;
import com.bartlomiejpluta.esa.file.cleaner.FileCleaner;
import com.bartlomiejpluta.esa.file.matcher.GlobMatcher;
import com.bartlomiejpluta.esa.file.provider.FileProvider;

import java.io.File;
import java.util.Set;

import static java.lang.String.format;

public class ApkAnalyser extends Analyser {
    private final Decompiler decompiler;
    private final FileCleaner fileCleaner;
    private final GlobMatcher globMatcher;

    public ApkAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, Decompiler decompiler, FileCleaner fileCleaner, GlobMatcher globMatcher, ContextConstructor contextConstructor) {
        super(pluginExecutor, plugins, fileProvider, contextConstructor);
        this.decompiler = decompiler;
        this.fileCleaner = fileCleaner;
        this.globMatcher = globMatcher;
    }

    @Override
    protected String prepareSources(String source, boolean debug) {
        checkIfSourceIsApkFile(source);
        System.out.println("Decompiling APK...");
        return decompiler.decompile(new File(source), debug).getAbsolutePath();
    }

    private void checkIfSourceIsApkFile(String source) {
        if (!globMatcher.fileMatchesGlobPattern(new File(source), "**/*.apk")) {
            throw new EsaException("Provided source is not *.apk file. Interrupting...");
        }
    }

    @Override
    protected String getAndroidManifestGlob() {
        return format("**/%s/AndroidManifest.xml", decompiler.getAndroidManifestFolder());
    }

    @Override
    protected String getJavaGlob() {
        return format("**/%s/**/*.java", decompiler.getJavaSourcesFolder());
    }

    @Override
    protected String getLayoutGlob() {
        return format("**/%s/layout*/*.xml", decompiler.getResFolder());
    }

    @Override
    protected void performCleaning(String source, boolean debug) {
        fileCleaner.deleteRecursively(new File(source));
    }
}
