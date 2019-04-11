package com.bartek.esa.decompiler.decompiler;

import com.bartek.esa.decompiler.archetype.Decompiler;
import com.bartek.esa.decompiler.process.ProcessExecutor;
import com.bartek.esa.file.cleaner.FileCleaner;
import com.bartek.esa.file.provider.FileProvider;
import com.bartek.esa.file.zip.ZipTool;
import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import java.io.File;
import java.util.Set;

public class CfrDecompiler implements Decompiler {
    private static final String XML_FILES_DIR = "xml";
    private static final String JAVA_FILES_DIR = "java";

    private static final String APK_UNZIPPED_DIR = "apk_unzipped";
    private static final String JAR_FILES_DIR = "jar";

    private final FileProvider fileProvider;
    private final ProcessExecutor processExecutor;
    private final ZipTool zipTool;
    private final FileCleaner fileCleaner;

    @Inject
    public CfrDecompiler(FileProvider fileProvider, ProcessExecutor processExecutor1, ZipTool zipTool, FileCleaner fileCleaner) {
        this.fileProvider = fileProvider;
        this.processExecutor = processExecutor1;
        this.zipTool = zipTool;
        this.fileCleaner = fileCleaner;
    }

    @Override
    public File decompile(File inputApk, boolean debug) {
        File tmp = fileProvider.createTemporaryDirectory();
        File javaDirectory = new File(tmp, JAVA_FILES_DIR);
        File xmlDirectory = new File(tmp, XML_FILES_DIR);

        decompileJavaFiles(inputApk, tmp, javaDirectory, debug);
        decompileXmlFiles(inputApk, xmlDirectory, debug);

        return tmp;
    }

    @Override
    public String getAndroidManifestFolder() {
        return "";
    }

    @Override
    public String getResFolder() {
        return XML_FILES_DIR;
    }

    @Override
    public String getJavaSourcesFolder() {
        return JAVA_FILES_DIR;
    }

    private void decompileJavaFiles(File inputApk, File tmp, File javaDirectory, boolean debug) {
        File unzippedApkDirectory = new File(tmp, APK_UNZIPPED_DIR);
        File jarDirectory = new File(tmp, JAR_FILES_DIR);

        zipTool.unzipArchive(inputApk, unzippedApkDirectory);

        Set<File> dexFiles = fileProvider.getGlobMatchedFiles(unzippedApkDirectory.getAbsolutePath(), "**/*.dex");
        convertDexToJar(dexFiles, jarDirectory, debug);

        Set<File> jarFiles = fileProvider.getGlobMatchedFiles(jarDirectory.getAbsolutePath(), "**/*.jar");
        decompileJar(jarFiles, javaDirectory, debug);

        fileCleaner.deleteRecursively(unzippedApkDirectory);
        fileCleaner.deleteRecursively(jarDirectory);
    }

    private void decompileXmlFiles(File inputApk, File target, boolean debug) {
        String[] command = {"apktool", "d", inputApk.getAbsolutePath(), "-o", target.getAbsolutePath()};
        processExecutor.execute(command, debug);
    }

    private void convertDexToJar(Set<File> dexFiles, File target, boolean debug) {
        dexFiles.forEach(dex -> {
            String jarFilename = FilenameUtils.removeExtension(dex.getName()) + ".jar";
            File jarFile = new File(target, jarFilename);
            String[] command = {"dex2jar", dex.getAbsolutePath(), "-o", jarFile.getAbsolutePath()};
            processExecutor.execute(command, debug);
        });
    }

    private void decompileJar(Set<File> jarFiles, File target, boolean debug) {
        jarFiles.forEach(jar -> {
            String[] command = {"cfr", jar.getAbsolutePath(), "--outputdir", target.getAbsolutePath()};
            processExecutor.execute(command, debug);
        });
    }
}
