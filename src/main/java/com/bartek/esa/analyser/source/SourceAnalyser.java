package com.bartek.esa.analyser.source;

import com.bartek.esa.analyser.core.Analyser;
import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.file.provider.FileProvider;

import java.util.Set;

public class SourceAnalyser extends Analyser {
    private static final String ANDROID_MANIFEST_GLOB = "**/AndroidManifest.xml";
    private static final String JAVA_GLOB = "**/*.java";
    private static final String LAYOUT_GLOB = "**/res/layout*/*.xml";

    public SourceAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider) {
        super(pluginExecutor, plugins, fileProvider);
    }

    @Override
    protected String prepareSources(String source) {
        return source;
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
