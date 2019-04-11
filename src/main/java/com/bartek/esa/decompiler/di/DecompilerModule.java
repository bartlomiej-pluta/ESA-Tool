package com.bartek.esa.decompiler.di;

import com.bartek.esa.decompiler.archetype.Decompiler;
import com.bartek.esa.decompiler.decompiler.JadxDecompiler;
import com.bartek.esa.decompiler.process.ProcessExecutor;
import com.bartek.esa.file.provider.FileProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class DecompilerModule {

    @Provides
    public ProcessExecutor processExecutor() {
        return new ProcessExecutor();
    }

    @Provides
    public Decompiler decompiler(FileProvider fileProvider, ProcessExecutor processExecutor) {
        return new JadxDecompiler(fileProvider, processExecutor);
    }
}
