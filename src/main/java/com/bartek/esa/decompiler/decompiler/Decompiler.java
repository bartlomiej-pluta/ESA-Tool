package com.bartek.esa.decompiler.decompiler;

import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.provider.FileProvider;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.io.File;

public class Decompiler {
    private final FileProvider fileProvider;

    @Inject
    public Decompiler(FileProvider fileProvider) {

        this.fileProvider = fileProvider;
    }

    public File decompile(File inputApk) {
        File target = provideTargetDirectory();
        Process process = decompile(inputApk, target);
        waitForProcess(process);
        checkExitValue(process);
        return target;
    }

    private void checkExitValue(Process process) {
        if(process.exitValue() != 0) {
            throw new EsaException("'apktool' process has finished with non-zero code. Interrupting...");
        }
    }

    private void waitForProcess(Process process) {
        Try.run(process::waitFor).getOrElseThrow(EsaException::new);
    }

    private Process decompile(File inputApk, File target) {
        return Try.of(() -> Runtime.getRuntime().exec(provideCommandArray(inputApk, target)))
                    .getOrElseThrow(EsaException::new);
    }

    private String[] provideCommandArray(File inputApk, File target) {
        return new String[]{"apktool", "d", inputApk.getAbsolutePath(), "-o", target.getAbsolutePath()};
    }

    private File provideTargetDirectory() {
        File tmp = fileProvider.createTemporaryDirectory();
        return new File(tmp, "apktool_out");
    }
}
