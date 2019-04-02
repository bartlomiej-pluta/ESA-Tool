package com.bartek.esa.core.executor;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PluginExecutor {
    private final XmlHelper xmlHelper;

    @Inject
    public PluginExecutor(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    public List<Issue> executeForFiles(File manifest, List<File> files, List<Plugin> plugins) {
        return files.stream()
                .map(file -> executeForFile(manifest, file, plugins))
                .flatMap(List::stream)
                .collect(toList());
    }

    private List<Issue> executeForFile(File manifest, File file, List<Plugin> plugins) {
        Document xmlManifest = xmlHelper.parseXml(manifest);
        return plugins.stream()
                .filter(plugin -> plugin.supports(file))
                .map(plugin -> {
                    plugin.update(file, xmlManifest);
                    return plugin.runForIssues();
                })
                .flatMap(List::stream)
                .collect(toList());
    }
}