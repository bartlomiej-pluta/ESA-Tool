package com.bartlomiejpluta.esa.analyser.core;

import com.bartlomiejpluta.esa.context.constructor.ContextConstructor;
import com.bartlomiejpluta.esa.context.model.Context;
import com.bartlomiejpluta.esa.core.archetype.Plugin;
import com.bartlomiejpluta.esa.core.executor.PluginExecutor;
import com.bartlomiejpluta.esa.core.model.object.Issue;
import com.bartlomiejpluta.esa.error.EsaException;
import com.bartlomiejpluta.esa.file.provider.FileProvider;

import java.io.File;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Analyser {
    private final PluginExecutor pluginExecutor;
    private final Set<Plugin> plugins;
    private final FileProvider fileProvider;
    private final ContextConstructor contextConstructor;

    public Analyser(PluginExecutor pluginExecutor, Set<Plugin> plugins, FileProvider fileProvider, ContextConstructor contextConstructor) {

        this.pluginExecutor = pluginExecutor;
        this.plugins = plugins;
        this.fileProvider = fileProvider;
        this.contextConstructor = contextConstructor;
    }

    public Set<Issue> analyse(String source, Set<String> pluginCodes, Set<String> excludeCodes, boolean debug) {
        String newSource = prepareSources(source, debug);
        File manifest = getManifest(newSource);
        Set<File> javaSources = getJavaSources(newSource);
        Set<File> layoutFiles = getLayoutFiles(newSource);
        Context context = contextConstructor.construct(manifest, javaSources, layoutFiles);

        Set<Plugin> selectedPlugins = getPlugins(pluginCodes, excludeCodes);
        Set<Issue> issues = pluginExecutor.executeForContext(context, selectedPlugins, debug);

        performCleaning(newSource, debug);
        return issues;
    }

    protected abstract String prepareSources(String source, boolean debug);

    protected abstract String getAndroidManifestGlob();

    protected abstract String getJavaGlob();

    protected abstract String getLayoutGlob();

    protected abstract void performCleaning(String source, boolean debug);


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

    private Set<File> getJavaSources(String source) {
        return fileProvider.getGlobMatchedFiles(source, getJavaGlob());
    }

    private Set<File> getLayoutFiles(String source) {
        return fileProvider.getGlobMatchedFiles(source, getLayoutGlob());
    }

    private Set<Plugin> getPlugins(Set<String> pluginCodes, Set<String> excludeCodes) {
        Set<Plugin> outputPlugins = plugins;

        if (!pluginCodes.isEmpty()) {
            outputPlugins = plugins.stream()
                    .filter(plugin -> pluginCodes
                            .stream()
                            .anyMatch(doesNameMatchPlugin(plugin))
                    )
                    .collect(Collectors.toSet());
        }

        if(!excludeCodes.isEmpty()) {
            outputPlugins = outputPlugins.stream()
                    .filter(plugin -> excludeCodes
                            .stream()
                            .noneMatch(doesNameMatchPlugin(plugin))
                    )
                    .collect(Collectors.toSet());
        }

        return outputPlugins;
    }

    private Predicate<String> doesNameMatchPlugin(Plugin plugin) {
        return pluginCode -> plugin.getClass().getCanonicalName().equals(pluginCode)
                || plugin.getClass().getSimpleName().equals(pluginCode);
    }
}
