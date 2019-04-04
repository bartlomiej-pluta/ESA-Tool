package com.bartek.esa.analyser.core;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.provider.FileProvider;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Analyser {
    private final PluginExecutor pluginExecutor;
    private final Set<Plugin> plugins;
    private final FileProvider fileProvider;

    public Analyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider) {

        this.pluginExecutor = pluginExecutor;
        this.plugins = plugins;
        this.fileProvider = fileProvider;
    }

    public List<Issue> analyse(String source, Set<String> pluginCodes, Set<String> excludeCodes) {
        String newSource = prepareSources(source);
        File manifest = getManifest(newSource);
        Set<File> files = getFiles(newSource);
        Set<Plugin> selectedPlugins = getPlugins(pluginCodes, excludeCodes);

        List<Issue> issues = pluginExecutor.executeForFiles(manifest, files, selectedPlugins);
        performCleaning(newSource);
        return issues;
    }

    protected abstract String prepareSources(String source);

    protected abstract String getAndroidManifestGlob();

    protected abstract String getJavaGlob();

    protected abstract String getLayoutGlob();

    protected abstract void performCleaning(String source);


    private File getManifest(String source) {
        Set<File> manifests = fileProvider.getGlobMatchedFiles(source, getAndroidManifestGlob());
        if (manifests.isEmpty()) {
            throw new EsaException("No AndroidManifest.xml file found. Interrupting...");
        }

        if (manifests.size() > 1) {
            throw new EsaException("Found multiple AndroidManifest.xml files. Interrupting...");
        }

        return (File) (manifests.toArray())[0];
    }

    private Set<File> getFiles(String source) {
        Set<File> javaFiles = fileProvider.getGlobMatchedFiles(source, getJavaGlob());
        Set<File> layoutFiles = fileProvider.getGlobMatchedFiles(source, getLayoutGlob());
        Set<File> androidManifest = Collections.singleton(getManifest(source));

        return Stream.of(javaFiles, androidManifest, layoutFiles)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Set<Plugin> getPlugins(Set<String> pluginCodes, Set<String> excludeCodes) {
        Set<Plugin> outputPlugins = plugins;

        if (!pluginCodes.isEmpty()) {
            outputPlugins = plugins.stream()
                    .filter(plugin -> pluginCodes
                            .stream()
                            .anyMatch(pluginCode -> plugin.getClass().getCanonicalName().equals(pluginCode))
                    )
                    .collect(Collectors.toSet());
        }

        if(!excludeCodes.isEmpty()) {
            outputPlugins = outputPlugins.stream()
                    .filter(plugin -> excludeCodes
                            .stream()
                            .noneMatch(pluginCode -> plugin.getClass().getCanonicalName().equals(pluginCode))
                    )
                    .collect(Collectors.toSet());
        }

        return outputPlugins;
    }
}
