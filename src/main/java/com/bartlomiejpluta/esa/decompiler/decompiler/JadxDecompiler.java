package com.bartlomiejpluta.esa.decompiler.decompiler;

import com.bartlomiejpluta.esa.decompiler.archetype.Decompiler;
import com.bartlomiejpluta.esa.decompiler.process.ProcessExecutor;
import com.bartlomiejpluta.esa.file.provider.FileProvider;

import javax.inject.Inject;
import java.io.File;

public class JadxDecompiler implements Decompiler {
    private static final String RESOURCES_FILES_DIR = "resources";
    private static final String JAVA_FILES_DIR = "sources";

    private final FileProvider fileProvider;
    private final ProcessExecutor processExecutor;

    @Inject
    public JadxDecompiler(FileProvider fileProvider, ProcessExecutor processExecutor) {
        this.fileProvider = fileProvider;
        this.processExecutor = processExecutor;
    }

    @Override
    public File decompile(File inputApk, boolean debug) {
        File tmp = fileProvider.createTemporaryDirectory();
        runJadx(inputApk, tmp, debug);
        return tmp;
    }

    private void runJadx(File inputApk, File tmp, boolean debug) {
        String[] cmd = {
                "jadx", inputApk.getAbsolutePath(),
                "-ds", new File(tmp, JAVA_FILES_DIR).getAbsolutePath(),
                "-dr", new File(tmp, RESOURCES_FILES_DIR).getAbsolutePath()
        };
        processExecutor.execute(cmd, debug);
    }

    @Override
    public String getAndroidManifestFolder() {
        return RESOURCES_FILES_DIR;
    }

    @Override
    public String getResFolder() {
        return RESOURCES_FILES_DIR + "/res";
    }

    @Override
    public String getJavaSourcesFolder() {
        return JAVA_FILES_DIR;
    }
}
