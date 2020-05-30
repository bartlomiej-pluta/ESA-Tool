package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.helper.StaticScopeHelper;
import com.bartek.esa.core.helper.StringConcatenationChecker;
import com.bartek.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class LoggingPlugin extends JavaPlugin {
    private final StaticScopeHelper staticScopeHelper;
    private final StringConcatenationChecker stringConcatenationChecker;

    @Inject
    public LoggingPlugin(StaticScopeHelper staticScopeHelper, StringConcatenationChecker stringConcatenationChecker) {
        this.staticScopeHelper = staticScopeHelper;
        this.stringConcatenationChecker = stringConcatenationChecker;
    }

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("v|d|i|w|e|wtf"))
                .filter(staticScopeHelper.isFromScope(java.getModel(), "v|d|i|w|e|wtf", "Log", "android.util"))
                .filter(expr -> expr.getArguments().size() >= 2)
                .filter(expr -> stringConcatenationChecker.isStringConcatenation(java.getModel(), expr.getArguments().get(1)))
                .forEach(expr -> addJavaIssue(Severity.INFO, java.getFile(), expr));
    }
}
