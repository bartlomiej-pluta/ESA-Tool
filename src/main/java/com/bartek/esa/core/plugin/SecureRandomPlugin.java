package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class SecureRandomPlugin extends JavaPlugin {

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("SecureRandom"))
                .filter(expr -> expr.getArguments().isNonEmpty())
                .forEach(expr -> addJavaIssue(Severity.VULNERABILITY, java.getFile(), expr));
    }
}
