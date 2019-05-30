package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class OrderedBroadcastPlugin extends JavaPlugin {

    @Inject
    public OrderedBroadcastPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("sendOrderedBroadcast|sendOrderedBroadcastAsUser|sendStickyOrderedBroadcast|sendStickyOrderedBroadcastAsUser"))
                .forEach(expr -> addIssue(Severity.WARNING, getLineNumberFromExpression(expr), expr.toString()));
    }
}
