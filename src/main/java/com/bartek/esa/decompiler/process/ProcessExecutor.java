package com.bartek.esa.decompiler.process;

import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;

import javax.inject.Inject;

public class ProcessExecutor {

    @Inject
    public ProcessExecutor() {

    }

    public void execute(String[] command) {
        Process process = Try.of(() -> Runtime.getRuntime().exec(command))
                .getOrElseThrow(EsaException::new);
        waitForProcess(process);
        checkExitValue(process, command[0]);
    }

    private void waitForProcess(Process process) {
        Try.run(process::waitFor).getOrElseThrow(EsaException::new);
    }

    private void checkExitValue(Process process, String commandName) {
        if(process.exitValue() != 0) {
            throw new EsaException("'" + commandName + "' process has finished with non-zero code. Interrupting...");
        }
    }
}
