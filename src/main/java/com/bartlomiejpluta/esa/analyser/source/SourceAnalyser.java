package com.bartlomiejpluta.esa.analyser.source;

import com.bartlomiejpluta.esa.analyser.core.Analyser;
import com.bartlomiejpluta.esa.context.constructor.ContextConstructor;
import com.bartlomiejpluta.esa.core.archetype.Plugin;
import com.bartlomiejpluta.esa.core.executor.PluginExecutor;
import com.bartlomiejpluta.esa.error.EsaException;
import com.bartlomiejpluta.esa.file.provider.FileProvider;

import java.io.File;
import java.util.Set;

public class SourceAnalyser extends Analyser {
    private static final String ANDROID_MANIFEST_GLOB = "**/AndroidManifest.xml";
    private static final String JAVA_GLOB = "**/*.java";
    private static final String LAYOUT_GLOB = "**/res/layout*/*.xml";

    public SourceAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, ContextConstructor contextConstructor) {
        super(pluginExecutor, plugins, fileProvider, contextConstructor);
    }

    @Override
    protected String prepareSources(String source, boolean debug) {
        checkIfSourceIsDirectory(source);
        return source;
    }

    private void checkIfSourceIsDirectory(String source) {
        if (!new File(source).isDirectory()) {
            throw new EsaException("Provided source is not a directory. Interrupting...");
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
        // do nothing
    }
}
