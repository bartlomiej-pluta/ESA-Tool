package com.bartlomiejpluta.esa.core.archetype;

import com.bartlomiejpluta.esa.context.model.Context;
import com.bartlomiejpluta.esa.context.model.Source;
import com.github.javaparser.ast.CompilationUnit;

public abstract class JavaPlugin extends BasePlugin {

    @Override
    protected void run(Context context) {
        context.getJavaSources().forEach(this::run);
    }

    protected abstract void run(Source<CompilationUnit> compilationUnit);
}
