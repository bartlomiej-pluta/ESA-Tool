package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.JavaPlugin;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;

public class TelephonyManagerPlugin extends JavaPlugin {

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(CastExpr.class).stream()
                .filter(expr -> expr.getType().isClassOrInterfaceType())
                .filter(expr -> expr.getType().asClassOrInterfaceType().getName().getIdentifier().equals("TelephonyManager"))
                .forEach(expr -> addJavaIssue(Severity.INFO, java.getFile(), expr));
    }
}
