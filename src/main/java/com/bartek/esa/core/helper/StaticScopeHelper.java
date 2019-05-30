package com.bartek.esa.core.helper;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.function.Predicate;

import static java.lang.String.format;

public class StaticScopeHelper {

    @Inject
    public StaticScopeHelper() {

    }

    public Predicate<MethodCallExpr> isFromScope(CompilationUnit compilationUnit, String methodName, String scope, String importScope) {
        return expr -> {
            boolean isFromScope =  expr.getScope()
                    .filter(Expression::isNameExpr)
                    .map(Expression::asNameExpr)
                    .map(NameExpr::getName)
                    .map(SimpleName::getIdentifier)
                    .map(s -> s.equals(scope))
                    .orElse(false);

            if(!isFromScope) {
                isFromScope = compilationUnit.findAll(ImportDeclaration.class).stream()
                        .filter(ImportDeclaration::isStatic)
                        .filter(e -> e.getName().getIdentifier().matches(methodName))
                        .map(ImportDeclaration::getName)
                        .map(Name::getQualifier)
                        .flatMap(Optional::stream)
                        .map(Node::toString)
                        .anyMatch(q -> q.equals(format("%s.%s", importScope, scope)));
            }

            if(!isFromScope) {
                isFromScope = compilationUnit.findAll(ImportDeclaration.class).stream()
                        .filter(ImportDeclaration::isStatic)
                        .filter(ImportDeclaration::isAsterisk)
                        .map(ImportDeclaration::getName)
                        .map(Name::getQualifier)
                        .flatMap(Optional::stream)
                        .map(Node::toString)
                        .anyMatch(q -> q.equals(importScope));
            }

            return isFromScope;
        };
    }
}
