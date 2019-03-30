package com.bartek.esa.file.provider;

import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class FileProvider {

    @Inject
    public FileProvider() {

    }

    public String readFile(File file) {
        StringBuilder content = new StringBuilder();
        Try.run(() -> Files.lines(file.toPath())
                .map(line -> line + "\n")
                .forEach(content::append));

        return content.toString();
    }

    public Set<File> findFilesRecursively(String path, String globPattern) {
        return findFilesRecursivelyInSubpath(path, "", globPattern);
    }

    public Set<File> findFilesRecursivelyInSubpath(String path, String subpath, String globPattern) {
        return Try.of(() -> Files.walk(Paths.get(path))
                .filter(p -> p.toString().contains(subpath))
                .filter(p -> matchesGlobPattern(p.getFileName().toString(), globPattern))
                .map(Path::toFile)
                .collect(Collectors.toSet()))
                .getOrElseThrow(EsaException::new);
    }

    public boolean matchesGlobPattern(String filename, String globPattern) {
        return filename.matches(createRegexFromGlob(globPattern));
    }

    private String createRegexFromGlob(String glob) {
        StringBuilder out = new StringBuilder("^");

        glob
                .chars()
                .mapToObj(i -> (char) i)
                .map(this::globCharacterToRegexString)
                .forEach(out::append);

        out.append('$');

        return out.toString();
    }

    private String globCharacterToRegexString(char character) {
        switch (character) {
            case '*':
                return ".*";
            case '?':
                return ".";
            case '.':
                return "\\.";
            case '\\':
                return "\\\\";
            default:
                return String.valueOf(character);
        }
    }
}
