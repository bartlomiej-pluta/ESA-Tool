package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class WebViewPlugin extends JavaPlugin {
    private static final String SETTINGS_METHODS = "addJavascriptInterface|setJavaScriptEnabled|setWebContentsDebuggingEnabled|setAllowFileAccess|setDomStorageEnabled";

    @Inject
    public WebViewPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches(SETTINGS_METHODS))
                .forEach(this::issueMethod);
    }

    private void issueMethod(MethodCallExpr methodCall) {
        switch (methodCall.getName().getIdentifier()) {
            case "addJavascriptInterface":
                addIssue(Severity.VULNERABILITY, ".JS_INTERFACE", getLineNumberFromExpression(methodCall), methodCall.toString());
                break;
            case "setJavaScriptEnabled":
                issueSettingsMethod(methodCall, ".JS_ENABLED");
                break;
            case "setWebContentsDebuggingEnabled":
                issueSettingsMethod(methodCall, ".DEBUGGING_ENABLED");
                break;
            case "setAllowFileAccess":
                issueSettingsMethod(methodCall, ".ALLOW_FILE_ACCESS");
                break;
        }
    }

    private void issueSettingsMethod(MethodCallExpr methodCall, String descriptionCode) {
        Expression firstArg = methodCall.getArguments().get(0);
        if (firstArg.isBooleanLiteralExpr() && firstArg.asBooleanLiteralExpr().getValue()) {
            addIssue(Severity.INFO, descriptionCode, getLineNumberFromExpression(methodCall), methodCall.toString());
        }
    }
}
