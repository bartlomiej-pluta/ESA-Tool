package com.bartek.esa.analyser.core;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.core.model.object.Issue;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Analyser {
    private final PluginExecutor pluginExecutor;
    private final Set<Plugin> plugins;

    public Analyser(PluginExecutor pluginExecutor, Set<Plugin> plugins) {

        this.pluginExecutor = pluginExecutor;
        this.plugins = plugins;
    }

    public List<Issue> analyse(String source, Set<String> pluginCodes, Set<String> excludeCodes) {
        File manifest = getManifest(source);
        Set<File> files = getFiles(source);
        Set<Plugin> selectedPlugins = getPlugins(pluginCodes, excludeCodes);

        return pluginExecutor.executeForFiles(manifest, files, selectedPlugins);
    }

    protected abstract File getManifest(String source);

    protected abstract Set<File> getFiles(String source);

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
