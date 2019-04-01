package com.bartek.esa.core.executor;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.model.Issue;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PluginExecutor {

    @Inject
    public PluginExecutor() {

    }

    public List<Issue> executeForFiles(List<File> files, List<Plugin> plugins) {
        return files.stream()
                .map(file -> executeForFile(file, plugins))
                .flatMap(List::stream)
                .collect(toList());
    }

    private List<Issue> executeForFile(File file, List<Plugin> plugins) {
        return plugins.stream()
                .filter(plugin -> plugin.supports(file))
                .map(plugin -> {
                    plugin.update(file);
                    return plugin.runForIssues();
                })
                .flatMap(List::stream)
                .collect(toList());
    }
}
