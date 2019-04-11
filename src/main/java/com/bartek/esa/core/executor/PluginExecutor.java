package com.bartek.esa.core.executor;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class PluginExecutor {
    private final XmlHelper xmlHelper;

    @Inject
    public PluginExecutor(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    public Set<Issue> executeForFiles(File manifest, Set<File> files, Set<Plugin> plugins, boolean debug) {
        return files.stream()
                .peek(file -> { if(debug) System.out.printf("File: %s", file.getAbsolutePath()); })
                .map(file -> executeForFile(manifest, file, plugins, debug))
                .flatMap(Set::stream)
                .collect(toSet());
    }

    private Set<Issue> executeForFile(File manifest, File file, Set<Plugin> plugins, boolean debug) {
        Document xmlManifest = xmlHelper.parseXml(manifest);
        return plugins.parallelStream()
                .peek(plugin -> { if(debug) System.out.printf(" Plugin: %s", plugin.getClass().getCanonicalName()); })
                .peek(plugin -> plugin.update(file, xmlManifest))
                .filter(plugin -> plugin.supports(file))
                .map(Plugin::runForIssues)
                .flatMap(Set::stream)
                .collect(toSet());
    }
}
