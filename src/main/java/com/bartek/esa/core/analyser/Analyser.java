package com.bartek.esa.core.analyser;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.provider.FileProvider;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Analyser {
    private final FileProvider fileProvider;
    private final PluginExecutor pluginExecutor;
    private final Set<Plugin> plugins;

    @Inject
    public Analyser(FileProvider fileProvider, PluginExecutor pluginExecutor, Set<Plugin> plugins) {
        this.fileProvider = fileProvider;

        this.pluginExecutor = pluginExecutor;
        this.plugins = plugins;
    }

    public List<Issue> analyse(String source, Set<String> pluginCodes, Set<String> excludeCodes) {
        File manifest = getManifest(source);
        Set<File> files = getFiles(source);
        Set<Plugin> selectedPlugins = getPlugins(pluginCodes, excludeCodes);

        return pluginExecutor.executeForFiles(manifest, files, selectedPlugins);
    }

    private File getManifest(String source) {
        Set<File> manifests = fileProvider.getGlobMatchedFiles(source, "**/AndroidManifest.xml");
        if (manifests.isEmpty()) {
            throw new EsaException("No AndroidManifest.xml file found. Interrupting...");
        }

        if (manifests.size() > 1) {
            throw new EsaException("Found multiple AndroidManifest.xml files. Interrupting...");
        }

        return (File) (manifests.toArray())[0];
    }

    private Set<File> getFiles(String source) {
        Set<File> javaFiles = fileProvider.getGlobMatchedFiles(source, "**/*.java");
        Set<File> androidManifest = fileProvider.getGlobMatchedFiles(source, "**/AndroidManifest.xml");
        Set<File> layoutFiles = fileProvider.getGlobMatchedFiles(source, "**/res/layout*/**.xml");

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
