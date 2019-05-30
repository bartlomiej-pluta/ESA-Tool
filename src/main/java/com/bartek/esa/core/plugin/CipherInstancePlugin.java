package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.helper.StaticScopeHelper;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;
import java.util.regex.Pattern;

public class CipherInstancePlugin extends JavaPlugin {
    private static final Pattern ALGORITHM_QUALIFIER = Pattern.compile("^\"\\w+/\\w+/\\w+\"$");
    private final StaticScopeHelper staticScopeHelper;

    @Inject
    public CipherInstancePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, StaticScopeHelper staticScopeHelper) {
        super(globMatcher, xmlHelper);
        this.staticScopeHelper = staticScopeHelper;
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("getInstance"))
                .filter(staticScopeHelper.isFromScope(compilationUnit, "getInstance", "Cipher", "javax.crypto"))
                .filter(expr -> expr.getArguments().isNonEmpty())
                .filter(expr -> !isFullCipherQualifier(expr.getArguments().get(0).toString()))
                .forEach(expr -> addIssue(Severity.ERROR, getLineNumberFromExpression(expr), expr.toString()));
    }

    private boolean isFullCipherQualifier(String qualifier) {
        return ALGORITHM_QUALIFIER.matcher(qualifier).matches();
    }
}
