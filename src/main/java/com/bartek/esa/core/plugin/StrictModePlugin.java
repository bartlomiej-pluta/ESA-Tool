package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.helper.StaticScopeHelper;
import com.bartek.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class StrictModePlugin extends JavaPlugin {
    private final StaticScopeHelper staticScopeHelper;

    @Inject
    public StrictModePlugin(StaticScopeHelper staticScopeHelper) {
        this.staticScopeHelper = staticScopeHelper;
    }

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("setThreadPolicy"))
                .filter(staticScopeHelper.isFromScope(java.getModel(), "setThreadPolicy", "StrictMode", "android.os"))
                .forEach(expr -> addJavaIssue(Severity.WARNING, java.getFile(), expr));
    }
}
