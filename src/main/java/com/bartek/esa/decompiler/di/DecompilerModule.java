package com.bartek.esa.decompiler.di;

import com.bartek.esa.decompiler.decompiler.Decompiler;
import dagger.Module;
import dagger.Provides;

@Module
public class DecompilerModule {

    @Provides
    public Decompiler decompiler() {
        return new Decompiler();
    }
}
