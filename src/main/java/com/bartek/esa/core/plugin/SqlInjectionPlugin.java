package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
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
                .filter(expr -> expr.getArguments().size() >= 2)
                .filter(this::isConcatenationOrMethodCall)
                .filter(this::ifMethodIsStringFormat)
                .forEach(expr -> addIssue(Severity.VULNERABILITY, getLineNumberFromExpression(expr), expr.toString()));
    }

    private boolean isConcatenationOrMethodCall(MethodCallExpr expr) {
        return expr.getArguments().get(0).isBinaryExpr() || expr.getArguments().get(0).isMethodCallExpr();
    }

    private boolean ifMethodIsStringFormat(MethodCallExpr expr) {
        if(expr.getArguments().get(0).isMethodCallExpr()) {
            return expr.getArguments().get(0).asMethodCallExpr().getName().getIdentifier().equals("format");
        }

        return true;
    }
}
