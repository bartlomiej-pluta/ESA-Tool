package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AnnotationExpr;

public class SuppressWarningsPlugin extends JavaPlugin {

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(AnnotationExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("SuppressWarnings"))
                .forEach(expr -> addJavaIssue(Severity.WARNING, java.getFile(), expr));
    }
}
