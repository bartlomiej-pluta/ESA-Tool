package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.JavaPlugin;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class OrderedBroadcastPlugin extends JavaPlugin {

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("sendOrderedBroadcast|sendOrderedBroadcastAsUser|sendStickyOrderedBroadcast|sendStickyOrderedBroadcastAsUser"))
                .forEach(expr -> addJavaIssue(Severity.WARNING, java.getFile(), expr));
    }
}
