package com.bartek.esa.decompiler.di;

import com.bartek.esa.decompiler.decompiler.Decompiler;
import com.bartek.esa.decompiler.process.ProcessExecutor;
import com.bartek.esa.file.cleaner.FileCleaner;
import com.bartek.esa.file.provider.FileProvider;
import com.bartek.esa.file.zip.ZipTool;
import dagger.Module;
import dagger.Provides;

@Module
public class DecompilerModule {

    @Provides
    public ProcessExecutor processExecutor() {
        return new ProcessExecutor();
    }

    @Provides
    public Decompiler decompiler(FileProvider fileProvider, ProcessExecutor processExecutor, ZipTool zipTool, FileCleaner fileCleaner) {
        return new Decompiler(fileProvider, processExecutor, zipTool, fileCleaner);
    }
}
