package com.bartek.esa.di;

import com.bartek.esa.EsaMain;
import com.bartek.esa.analyser.di.AnalyserModule;
import com.bartek.esa.cli.di.CliModule;
import com.bartek.esa.core.di.CoreModule;
import com.bartek.esa.core.di.PluginModule;
import com.bartek.esa.decompiler.di.DecompilerModule;
import com.bartek.esa.dispatcher.di.DispatcherModule;
import com.bartek.esa.file.di.FileModule;
import dagger.Component;

@Component(modules = {
        CliModule.class,
        DispatcherModule.class,
        FileModule.class,
        DecompilerModule.class,
        CoreModule.class,
        PluginModule.class,
        AnalyserModule.class
})
public interface DependencyInjector {
    EsaMain esa();
}
