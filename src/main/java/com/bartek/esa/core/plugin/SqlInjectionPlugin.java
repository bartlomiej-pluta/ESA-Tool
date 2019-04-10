package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class SqlInjectionPlugin extends JavaPlugin {

    @Inject
    public SqlInjectionPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("rawQuery"))
                .forEach(expr -> addIssue(Severity.VULNERABILITY, getLineNumberFromExpression(expr), expr.toString()));
    }
}
