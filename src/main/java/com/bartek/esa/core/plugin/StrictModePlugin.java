package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.helper.StaticScopeHelper;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class StrictModePlugin extends JavaPlugin {
    private final StaticScopeHelper staticScopeHelper;

    @Inject
    public StrictModePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, StaticScopeHelper staticScopeHelper) {
        super(globMatcher, xmlHelper);
        this.staticScopeHelper = staticScopeHelper;
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("setThreadPolicy"))
                .filter(staticScopeHelper.isFromScope(compilationUnit, "setThreadPolicy", "StrictMode", "android.os"))
                .forEach(expr -> addIssue(Severity.WARNING, getLineNumberFromExpression(expr), expr.toString()));
    }
}
