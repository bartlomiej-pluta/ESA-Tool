package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.JavaPlugin;
import com.bartlomiejpluta.esa.core.helper.StaticScopeHelper;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;
import java.util.regex.Pattern;

public class CipherInstancePlugin extends JavaPlugin {
    private static final Pattern ALGORITHM_QUALIFIER = Pattern.compile("^\"\\w+/\\w+/\\w+\"$");
    private final StaticScopeHelper staticScopeHelper;

    @Inject
    public CipherInstancePlugin(StaticScopeHelper staticScopeHelper) {
        this.staticScopeHelper = staticScopeHelper;
    }

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("getInstance"))
                .filter(staticScopeHelper.isFromScope(java.getModel(), "getInstance", "Cipher", "javax.crypto"))
                .filter(expr -> expr.getArguments().isNonEmpty())
                .filter(expr -> !isFullCipherQualifier(expr.getArguments().get(0).toString()))
                .forEach(expr -> addJavaIssue(Severity.ERROR, java.getFile(), expr));
    }

    private boolean isFullCipherQualifier(String qualifier) {
        return ALGORITHM_QUALIFIER.matcher(qualifier).matches();
    }
}
