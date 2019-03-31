package com.bartek.esa.file.provider;

import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.model.FoundLine;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileContentProvider {

    @Inject
    public FileContentProvider() {

    }

    public List<FoundLine> findPhraseByLines(File file, String regex) {
        try {
            return tryToFindPhraseByLines(file, regex);
        } catch (IOException e) {
            throw new EsaException(e);
        }
    }

    private List<FoundLine> tryToFindPhraseByLines(File file, String regex) throws IOException {
        LineNumberReader lineNumberReader = readForLines(file);
        List<FoundLine> foundLines = new ArrayList<>();

        String line;
        Pattern pattern = Pattern.compile(regex);
        while ((line = lineNumberReader.readLine()) != null) {
            if (pattern.matcher(line).find()) {
                foundLines.add(FoundLine.builder()
                        .line(line)
                        .number(lineNumberReader.getLineNumber())
                        .build());
            }
        }

        return foundLines;
    }

    private LineNumberReader readForLines(File file) {
        return Try.of(() -> new LineNumberReader(new FileReader(file)))
                .getOrElseThrow(EsaException::new);
    }

    public String readFile(File file) {
        StringBuilder content = new StringBuilder();
        Try.run(() -> Files.lines(file.toPath())
                .map(line -> line + "\n")
                .forEach(content::append));

        return content.toString();
    }
}
