package com.bartek.esa.core.di;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.plugin.LoggingPlugin;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import dagger.multibindings.IntoSet;

import java.util.HashSet;
import java.util.Set;

@Module
public class PluginModule {

    @Provides
    @ElementsIntoSet
    public Set<Plugin> plugins() {
        return new HashSet<>();
    }

    @Provides
    @IntoSet
    public Plugin loggingPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new LoggingPlugin(globMatcher, xmlHelper);
    }
}
