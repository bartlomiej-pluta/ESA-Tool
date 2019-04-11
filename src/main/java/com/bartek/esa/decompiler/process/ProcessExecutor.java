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
    }

    private void printCommandLine(String[] command, boolean debug) {
        if (debug) {
            System.out.println(String.join(" ", command));
        }
    }

    private void printStdOutAndStdErrFromProcess(boolean debug, Process process) {
        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        try {
            while ((line = stdout.readLine()) != null) {
                if (debug) {
                    System.out.println(line);
                }
            }

            while ((line = stderr.readLine()) != null) {
                if (debug) {
                    System.err.println(line);
                }
            }

            stdout.close();
            stderr.close();
        } catch (IOException e) {
            throw new EsaException(e);
        }
    }

    private void waitForProcess(Process process) {
        Try.run(process::waitFor).getOrElseThrow(EsaException::new);
    }
}
