package com.bartek.esa.di;

import com.bartek.esa.EsaMain;
import com.bartek.esa.cli.di.CliModule;
import dagger.Component;

@Component(modules = {
        CliModule.class
})
public interface DependencyInjector {
    EsaMain esa();
}
