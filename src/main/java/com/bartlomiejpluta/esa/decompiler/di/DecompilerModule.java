package com.bartlomiejpluta.esa.decompiler.di;

import com.bartlomiejpluta.esa.decompiler.archetype.Decompiler;
import com.bartlomiejpluta.esa.decompiler.decompiler.JadxDecompiler;
import com.bartlomiejpluta.esa.decompiler.process.ProcessExecutor;
import com.bartlomiejpluta.esa.file.provider.FileProvider;
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
