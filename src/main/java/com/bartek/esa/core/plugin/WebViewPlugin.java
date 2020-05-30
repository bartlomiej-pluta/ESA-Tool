package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.function.Consumer;

public class WebViewPlugin extends JavaPlugin {
    private static final String SETTINGS_METHODS = "addJavascriptInterface|setJavaScriptEnabled|setWebContentsDebuggingEnabled|setAllowFileAccess|setDomStorageEnabled";

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches(SETTINGS_METHODS))
                .forEach(issueMethod(java));
    }

    private Consumer<MethodCallExpr> issueMethod(Source<CompilationUnit> java) {
        return methodCall -> {
            switch (methodCall.getName().getIdentifier()) {
                case "addJavascriptInterface":
                    addJavaIssue(Severity.VULNERABILITY, ".JS_INTERFACE", java.getFile(), methodCall);
                    break;
                case "setJavaScriptEnabled":
                    issueSettingsMethod(java, methodCall, ".JS_ENABLED");
                    break;
                case "setWebContentsDebuggingEnabled":
                    issueSettingsMethod(java, methodCall, ".DEBUGGING_ENABLED");
                    break;
                case "setAllowFileAccess":
                    issueSettingsMethod(java, methodCall, ".ALLOW_FILE_ACCESS");
                    break;
            }
        };
    }

    private void issueSettingsMethod(Source<CompilationUnit> java, MethodCallExpr methodCall, String descriptionCode) {
        Expression firstArg = methodCall.getArguments().get(0);
        if (firstArg.isBooleanLiteralExpr() && firstArg.asBooleanLiteralExpr().getValue()) {
            addJavaIssue(Severity.WARNING, descriptionCode, java.getFile(), methodCall);
        }
    }
}
