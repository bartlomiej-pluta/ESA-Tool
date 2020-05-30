package com.bartlomiejpluta.esa.file.cleaner;

import com.bartlomiejpluta.esa.error.EsaException;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public class FileCleaner {

    @Inject
    public FileCleaner() {

    }

    public void deleteRecursively(File directory) {
        Try.run(() -> Files.walk(directory.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete))
                .getOrElseThrow(EsaException::new);
    }
}
