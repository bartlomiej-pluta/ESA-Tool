package com.bartek.esa.analyser.source;

import com.bartek.esa.analyser.core.Analyser;
import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.provider.FileProvider;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SourceAnalyser extends Analyser {
    private final FileProvider fileProvider;

    @Inject
    public SourceAnalyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider) {
        super(pluginExecutor, plugins);
        this.fileProvider = fileProvider;
    }

    @Override
    protected File getManifest(String source) {
        Set<File> manifests = fileProvider.getGlobMatchedFilesRecursively(source, "**/AndroidManifest.xml");
        if (manifests.isEmpty()) {
            throw new EsaException("No AndroidManifest.xml file found. Interrupting...");
        }

        if (manifests.size() > 1) {
            throw new EsaException("Found multiple AndroidManifest.xml files. Interrupting...");
        }

        return (File) (manifests.toArray())[0];
    }

    @Override
    protected Set<File> getFiles(String source) {
        Set<File> javaFiles = fileProvider.getGlobMatchedFilesRecursively(source, "**/*.java");
        System.out.println(javaFiles);
        Set<File> layoutFiles = fileProvider.getGlobMatchedFilesRecursively(source, "**/res/layout*/**.xml");
        System.out.println(layoutFiles);
        Set<File> androidManifest = Collections.singleton(getManifest(source));
        System.out.println(androidManifest);

        return Stream.of(javaFiles, androidManifest, layoutFiles)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
