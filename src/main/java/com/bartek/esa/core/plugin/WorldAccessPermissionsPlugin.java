package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

import javax.inject.Inject;
import java.util.Map;

public class WorldAccessPermissionsPlugin extends JavaPlugin {

    @Inject
    public WorldAccessPermissionsPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(NameExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("MODE_WORLD_(READABLE|WRITEABLE)"))
                .forEach(expr -> addIssue(Severity.ERROR, getModel(expr), getLineNumberFromExpression(expr), expr.toString()));

        compilationUnit.findAll(FieldAccessExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("MODE_WORLD_(READABLE|WRITEABLE)"))
                .forEach(expr -> addIssue(Severity.ERROR, getModel(expr), getLineNumberFromExpression(expr), expr.toString()));
    }

    private Map<String, String> getModel(NameExpr expression) {
        return Map.of("exprName", expression.getName().getIdentifier());
    }

    private Map<String, String> getModel(FieldAccessExpr expression) {
        return Map.of("exprName", expression.getName().getIdentifier());
    }
}
