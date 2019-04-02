package com.bartek.esa.analyser.apk;

import com.bartek.esa.analyser.core.Analyser;
import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.decompiler.decompiler.Decompiler;
import com.bartek.esa.file.provider.FileProvider;

import java.io.File;
import java.util.Set;

public class ApkAnalyser extends Analyser {
    private static final String ANDROID_MANIFEST_GLOB = "**/" + Decompiler.XML_FILES_DIR + "/AndroidManifest.xml";
    private static final String JAVA_GLOB = "**/" + Decompiler.JAVA_FILES_DIR + "/**/*.java";
    private static final String LAYOUT_GLOB = "**/" + Decompiler.XML_FILES_DIR + "/**/layout*/*.xml";

    private final Decompiler decompiler;

    public ApkAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, Decompiler decompiler) {
        super(pluginExecutor, plugins, fileProvider);
        this.decompiler = decompiler;
    }

    @Override
    protected String prepareSources(String source) {
        return decompiler.decompile(new File(source)).getAbsolutePath();
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
}
