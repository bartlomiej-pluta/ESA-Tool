package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.regex.Pattern;

public class CipherInstancePlugin extends JavaPlugin {
    private static final Pattern ALGORITHM_QUALIFIER = Pattern.compile("^\"\\w+/\\w+/\\w+\"$");

    @Inject
    public CipherInstancePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("getInstance"))
                .filter(expr -> isCipherMethod(expr, compilationUnit))
                .filter(expr -> expr.getArguments().isNonEmpty())
                .filter(expr -> !isFullCipherQualifier(expr.getArguments().get(0).toString()))
                .forEach(expr -> addIssue(Severity.ERROR, getLineNumberFromExpression(expr), expr.toString()));
    }

    private boolean isCipherMethod(MethodCallExpr expr, CompilationUnit compilationUnit) {
        boolean isCipherMethod = expr.getScope()
                .filter(Expression::isNameExpr)
                .map(Expression::asNameExpr)
                .map(NameExpr::getName)
                .map(SimpleName::getIdentifier)
                .filter(name -> name.equals("Cipher"))
                .isPresent();

        if(!isCipherMethod) {
            isCipherMethod = compilationUnit.findAll(ImportDeclaration.class).stream()
                    .filter(ImportDeclaration::isStatic)
                    .filter(e -> e.getName().getIdentifier().equals("getInstance"))
                    .map(ImportDeclaration::getName)
                    .map(Name::getQualifier)
                    .flatMap(Optional::stream)
                    .map(Node::toString)
                    .anyMatch(q -> q.equals("javax.crypto.Cipher"));
        }

        if(!isCipherMethod) {
            isCipherMethod = compilationUnit.findAll(ImportDeclaration.class).stream()
                    .filter(ImportDeclaration::isStatic)
                    .filter(ImportDeclaration::isAsterisk)
                    .map(ImportDeclaration::getName)
                    .map(Name::getQualifier)
                    .flatMap(Optional::stream)
                    .map(Node::toString)
                    .anyMatch(q -> q.equals("javax.crypto"));
        }

        return isCipherMethod;
    }

    private boolean isFullCipherQualifier(String qualifier) {
        return ALGORITHM_QUALIFIER.matcher(qualifier).matches();
    }
}
