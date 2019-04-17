package com.bartek.esa.cli.printer;

import com.bartek.esa.core.archetype.Plugin;

import javax.inject.Inject;
import java.util.Set;

import static java.lang.String.format;

public class PluginPrinter {
    private final Set<Plugin> plugins;

    @Inject
    public PluginPrinter(Set<Plugin> plugins) {
        this.plugins = plugins;
    }

    public void printPlugins() {
        System.out.println("List of available plugins");
        System.out.println(format("%-33s | Fully qualified plugin code", "Simple plugin code"));
        plugins.stream()
                .map(Object::getClass)
                .map(p -> format(" - %-30s | - %s", p.getSimpleName(), p.getCanonicalName()))
                .forEach(System.out::println);
    }
}
