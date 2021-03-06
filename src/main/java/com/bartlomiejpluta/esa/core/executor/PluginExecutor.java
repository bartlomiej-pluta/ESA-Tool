package com.bartlomiejpluta.esa.core.executor;

import com.bartlomiejpluta.esa.context.model.Context;
import com.bartlomiejpluta.esa.core.archetype.Plugin;
import com.bartlomiejpluta.esa.core.model.object.Issue;

import java.util.Set;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toSet;

public class PluginExecutor {

    public Set<Issue> executeForContext(Context context, Set<Plugin> plugins, boolean debug) {
        return plugins.stream()
                .peek(logPlugin(debug))
                .map(plugin -> plugin.runForIssues(context))
                .flatMap(Set::stream)
                .collect(toSet());

    }

    private Consumer<Plugin> logPlugin(boolean debug) {
        return plugin -> {
            if(debug) {
                System.out.printf(" Plugin: %s\n", plugin.getClass().getCanonicalName());
            }
        };
    }
}
