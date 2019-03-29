package com.bartek.esa.di;

import com.bartek.esa.EsaMain;
import com.bartek.esa.cli.di.CliModule;
import com.bartek.esa.dispatcher.di.DispatcherModule;
import dagger.Component;

@Component(modules = {
        CliModule.class,
        DispatcherModule.class
})
public interface DependencyInjector {
    EsaMain esa();
}
