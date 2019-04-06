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

public class StrictModePlugin extends JavaPlugin {

    @Inject
    public StrictModePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().equals("setThreadPolicy"))
                .filter(expr -> isStrictModeScope(expr, compilationUnit))
                .forEach(expr -> addIssue(Severity.INFO, getLineNumberFromExpression(expr), expr.toString()));
    }

    private boolean isStrictModeScope(MethodCallExpr expr, CompilationUnit compilationUnit) {
        boolean isStrictModeScope =  expr.getScope()
                .filter(Expression::isNameExpr)
                .map(Expression::asNameExpr)
                .map(NameExpr::getName)
                .map(SimpleName::getIdentifier)
                .map(s -> s.equals("StrictMode"))
                .orElse(false);

        if(!isStrictModeScope) {
            isStrictModeScope = compilationUnit.findAll(ImportDeclaration.class).stream()
                    .filter(ImportDeclaration::isStatic)
                    .filter(e -> e.getName().getIdentifier().equals("setThreadPolicy"))
                    .map(ImportDeclaration::getName)
                    .map(Name::getQualifier)
                    .flatMap(Optional::stream)
                    .map(Node::toString)
                    .anyMatch(q -> q.equals("android.os.StrictMode"));
        }

        if(!isStrictModeScope) {
            isStrictModeScope = compilationUnit.findAll(ImportDeclaration.class).stream()
                    .filter(ImportDeclaration::isStatic)
                    .filter(ImportDeclaration::isAsterisk)
                    .map(ImportDeclaration::getName)
                    .map(Name::getQualifier)
                    .flatMap(Optional::stream)
                    .map(Node::toString)
                    .anyMatch(q -> q.equals("android.os"));
        }

        return isStrictModeScope;
    }
}
