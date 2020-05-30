package com.bartlomiejpluta.esa.file.zip;

import com.bartlomiejpluta.esa.error.EsaException;
import io.vavr.control.Try;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTool {

    @Inject
    public ZipTool() {

    }

    public void unzipArchive(File archive, File outputDir) {
        Try.run(() -> tryToUnzipArchive(archive, outputDir))
                .getOrElseThrow(EsaException::new);
    }

    private void tryToUnzipArchive(File archive, File outputDir) throws IOException {
        ZipFile zipFile = new ZipFile(archive);

        zipFile.stream().forEach(entry -> unzipEntry(zipFile, entry, outputDir));
    }

    private void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir)  {
        if (entry.isDirectory()) {
            createDir(new File(outputDir, entry.getName()));
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()){
            createDir(outputFile.getParentFile());
        }

        BufferedInputStream inputStream = Try.of(() -> new BufferedInputStream(zipfile.getInputStream(entry)))
                .getOrElseThrow(EsaException::new);
        BufferedOutputStream outputStream =  Try.of(() -> new BufferedOutputStream(new FileOutputStream(outputFile)))
                .getOrElseThrow(EsaException::new);

        try {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new EsaException(e);
        } finally {
            Try.run(outputStream::close).getOrElseThrow(EsaException::new);
            Try.run(inputStream::close).getOrElseThrow(EsaException::new);
        }
    }

    private void createDir(File dir) {
        if(!dir.mkdirs()) throw new EsaException("Cannot create directory " + dir);
    }
}
