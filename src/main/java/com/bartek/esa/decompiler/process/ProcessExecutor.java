package com.bartek.esa.decompiler.process;

import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessExecutor {

    @Inject
    public ProcessExecutor() {

    }

    public void execute(String[] command, boolean debug) {
        printCommandLine(command, debug);
        Process process = Try.of(() -> Runtime.getRuntime().exec(command))
                .getOrElseThrow(EsaException::new);
        printStdOutAndStdErrFromProcess(debug, process);
        waitForProcess(process);
        checkExitValue(process, command[0]);
    }

    private void printCommandLine(String[] command, boolean debug) {
        if(debug) {
            System.out.println(String.join(" ", command));
        }
    }

    private void printStdOutAndStdErrFromProcess(boolean debug, Process process) {
        if(debug) {
            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            try {
                while ((line = stdout.readLine()) != null) {
                    System.out.println(line);
                }

                while ((line = stderr.readLine()) != null) {
                    System.err.println(line);
                }

                stdout.close();
                stderr.close();
            } catch (IOException e) {
                throw new EsaException(e);
            }
        }
    }

    private void waitForProcess(Process process) {
        Try.run(process::waitFor).getOrElseThrow(EsaException::new);
    }

    private void checkExitValue(Process process, String commandName) {
        if (process.exitValue() != 0) {
            throw new EsaException("'" + commandName + "' process has finished with non-zero code. Interrupting...");
        }
    }
}
