package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

import javax.inject.Inject;

public class SecureRandomPlugin extends JavaPlugin {

    @Inject
    public SecureRandomPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("SecureRandom"))
                .filter(expr -> expr.getArguments().isNonEmpty())
                .forEach(expr -> addIssue(Severity.VULNERABILITY, getLineNumberFromExpression(expr), expr.toString()));
    }
}
