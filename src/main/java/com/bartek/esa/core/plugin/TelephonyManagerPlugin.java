package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.CastExpr;

import javax.inject.Inject;

public class TelephonyManagerPlugin extends JavaPlugin {

    @Inject
    public TelephonyManagerPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(CastExpr.class).stream()
                .filter(expr -> expr.getType().isClassOrInterfaceType())
                .filter(expr -> expr.getType().asClassOrInterfaceType().getName().getIdentifier().equals("TelephonyManager"))
                .forEach(expr -> addIssue(Severity.INFO, getLineNumberFromExpression(expr), expr.toString()));
    }
}
