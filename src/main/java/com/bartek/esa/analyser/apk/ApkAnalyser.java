package com.bartek.esa.analyser.apk;

import com.bartek.esa.analyser.core.Analyser;
import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.decompiler.decompiler.Decompiler;
import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.cleaner.FileCleaner;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.bartek.esa.file.provider.FileProvider;

import java.io.File;
import java.util.Set;

public class ApkAnalyser extends Analyser {
    private static final String ANDROID_MANIFEST_GLOB = "**/" + Decompiler.XML_FILES_DIR + "/AndroidManifest.xml";
    private static final String JAVA_GLOB = "**/" + Decompiler.JAVA_FILES_DIR + "/**/*.java";
    private static final String LAYOUT_GLOB = "**/" + Decompiler.XML_FILES_DIR + "/**/layout*/*.xml";

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
        return ANDROID_MANIFEST_GLOB;
    }

    @Override
    protected String getJavaGlob() {
        return JAVA_GLOB;
    }

    @Override
    protected String getLayoutGlob() {
        return LAYOUT_GLOB;
    }

    @Override
    protected void performCleaning(String source, boolean debug) {
        fileCleaner.deleteRecursively(new File(source));
    }
}
