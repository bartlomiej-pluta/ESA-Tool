package com.bartlomiejpluta.esa.di;

import com.bartlomiejpluta.esa.EsaMain;
import com.bartlomiejpluta.esa.analyser.di.AnalyserModule;
import com.bartlomiejpluta.esa.cli.di.CliModule;
import com.bartlomiejpluta.esa.context.di.ContextModule;
import com.bartlomiejpluta.esa.core.di.CoreModule;
import com.bartlomiejpluta.esa.core.di.PluginModule;
import com.bartlomiejpluta.esa.decompiler.di.DecompilerModule;
import com.bartlomiejpluta.esa.dispatcher.di.DispatcherModule;
import com.bartlomiejpluta.esa.file.di.FileModule;
import com.bartlomiejpluta.esa.formatter.di.FormatterModule;
import dagger.Component;

@Component(modules = {
        CliModule.class,
        DispatcherModule.class,
        FileModule.class,
        DecompilerModule.class,
        CoreModule.class,
        PluginModule.class,
        AnalyserModule.class,
        FormatterModule.class,
        ContextModule.class
})
public interface DependencyInjector {
    EsaMain esa();
}
