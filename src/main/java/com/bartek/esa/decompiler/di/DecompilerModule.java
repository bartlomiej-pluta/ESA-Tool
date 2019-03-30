package com.bartek.esa.decompiler.di;

import com.bartek.esa.decompiler.decompiler.Decompiler;
import com.bartek.esa.file.provider.FileProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class DecompilerModule {

    @Provides
    public Decompiler decompiler(FileProvider fileProvider) {
        return new Decompiler(fileProvider);
    }
}
