package com.bartlomiejpluta.esa.file.di;

import com.bartlomiejpluta.esa.file.cleaner.FileCleaner;
import com.bartlomiejpluta.esa.file.matcher.GlobMatcher;
import com.bartlomiejpluta.esa.file.matcher.PackageNameMatcher;
import com.bartlomiejpluta.esa.file.provider.FileContentProvider;
import com.bartlomiejpluta.esa.file.provider.FileProvider;
import com.bartlomiejpluta.esa.file.zip.ZipTool;
import dagger.Module;
import dagger.Provides;

@Module
public class FileModule {

    @Provides
    public FileProvider fileProvider(GlobMatcher globMatcher) {
        return new FileProvider(globMatcher);
    }

    @Provides
    public FileContentProvider fileContentProvider() {
        return new FileContentProvider();
    }

    @Provides
    public GlobMatcher globMatcher() {
        return new GlobMatcher();
    }

    @Provides
    public ZipTool zipTool() {
        return new ZipTool();
    }

    @Provides
    public FileCleaner fileCleaner() {
        return new FileCleaner();
    }

    @Provides
    public PackageNameMatcher packageNameMatcher() {
        return new PackageNameMatcher();
    }
}
