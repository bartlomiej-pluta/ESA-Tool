package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.JavaPlugin;
import com.bartlomiejpluta.esa.core.helper.StringConcatenationChecker;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class SqlInjectionPlugin extends JavaPlugin {
    private final StringConcatenationChecker stringConcatenationChecker;

    @Inject
    public SqlInjectionPlugin(StringConcatenationChecker stringConcatenationChecker) {
        this.stringConcatenationChecker = stringConcatenationChecker;
    }

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("rawQuery"))
                .filter(expr -> expr.getArguments().size() >= 2)
                .filter(expr -> stringConcatenationChecker.isStringConcatenation(java.getModel(), expr.getArguments().get(0)))
                .forEach(expr -> addJavaIssue(Severity.VULNERABILITY, java.getFile(), expr));
    }
}
