package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.JavaPlugin;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.util.Map;

public class WorldAccessPermissionsPlugin extends JavaPlugin {

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(NameExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("MODE_WORLD_(READABLE|WRITEABLE)"))
                .forEach(expr -> addJavaIssue(Severity.ERROR, getModel(expr), java.getFile(), expr));

        java.getModel().findAll(FieldAccessExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("MODE_WORLD_(READABLE|WRITEABLE)"))
                .forEach(expr -> addJavaIssue(Severity.ERROR, getModel(expr), java.getFile(), expr));
    }

    private Map<String, String> getModel(NameExpr expression) {
        return Map.of("exprName", expression.getName().getIdentifier());
    }

    private Map<String, String> getModel(FieldAccessExpr expression) {
        return Map.of("exprName", expression.getName().getIdentifier());
    }
}
