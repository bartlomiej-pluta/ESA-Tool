package com.bartek.esa.core.di;

import com.bartek.esa.core.executor.PluginExecutor;
import dagger.Module;
import dagger.Provides;

@Module
public class CoreModule {

    @Provides
    public PluginExecutor pluginExecutor() {
        return new PluginExecutor();
    }
}
