package com.bartek.esa.core.di;

import com.bartek.esa.core.archetype.Plugin;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;

import java.util.HashSet;
import java.util.Set;

@Module
public class PluginModule {

    @Provides
    @ElementsIntoSet
    public Set<Plugin> plugins() {
        return new HashSet<>();
    }
}
