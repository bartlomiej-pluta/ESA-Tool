package com.bartek.esa.file.provider;

import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.matcher.GlobMatcher;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class FileProvider {
    private final GlobMatcher globMatcher;

    @Inject
    public FileProvider(GlobMatcher globMatcher) {

        this.globMatcher = globMatcher;
    }

    public File createTemporaryDirectory() {
        return Try.of(() -> Files.createTempDirectory(null))
                .getOrElseThrow(EsaException::new)
                .toFile();
    }

    public Set<File> getGlobMatchedFilesRecursively(String path, String globPattern) {
        return Try.of(() -> Files.walk(Paths.get(path))
                .filter(p -> globMatcher.pathMatchesGlobPattern(p, globPattern))
                .map(Path::toFile)
                .collect(Collectors.toSet()))
                .getOrElseThrow(EsaException::new);
    }
}
