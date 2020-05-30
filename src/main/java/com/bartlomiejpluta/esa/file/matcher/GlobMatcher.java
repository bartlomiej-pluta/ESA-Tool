package com.bartlomiejpluta.esa.file.matcher;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class GlobMatcher {

    @Inject
    public GlobMatcher() {

    }

    public boolean fileMatchesGlobPattern(File file, String globPattern) {
        return pathMatchesGlobPattern(file.toPath(), globPattern);
    }

    public boolean pathMatchesGlobPattern(Path path, String globPattern) {
        return FileSystems.getDefault().getPathMatcher("glob:" + globPattern).matches(path);
    }
}
