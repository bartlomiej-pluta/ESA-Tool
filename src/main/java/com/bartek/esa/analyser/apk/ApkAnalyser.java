package com.bartek.esa.analyser.apk;

import com.bartek.esa.analyser.core.Analyser;
import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.decompiler.archetype.Decompiler;
import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.cleaner.FileCleaner;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.bartek.esa.file.provider.FileProvider;

import java.io.File;
import java.util.Set;

import static java.lang.String.format;

public class ApkAnalyser extends Analyser {
    private final Decompiler decompiler;
    private final FileCleaner fileCleaner;
    private final GlobMatcher globMatcher;

    public ApkAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, Decompiler decompiler, FileCleaner fileCleaner, GlobMatcher globMatcher) {
        super(pluginExecutor, plugins, fileProvider);
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
