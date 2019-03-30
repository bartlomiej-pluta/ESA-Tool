package com.bartek.esa.file.di;

import com.bartek.esa.file.provider.FileProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class FileModule {

    @Provides
    public FileProvider fileProvider() {
        return new FileProvider();
    }
}
