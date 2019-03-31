package com.bartek.esa.core.archetype;

import com.bartek.esa.error.EsaException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.vavr.control.Try;

import java.io.File;

public abstract class JavaPlugin extends BasePlugin {

    @Override
    protected void run(File file) {
        CompilationUnit compilationUnit = Try.of(() -> StaticJavaParser.parse(file)).getOrElseThrow(EsaException::new);
        run(compilationUnit);
    }

    public abstract void run(CompilationUnit compilationUnit);
}
