package com.bartlomiejpluta.esa.core.helper;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;
import java.util.function.Predicate;

public class StringConcatenationChecker {
    private final StaticScopeHelper staticScopeHelper;

    @Inject
    public StringConcatenationChecker(StaticScopeHelper staticScopeHelper) {

        this.staticScopeHelper = staticScopeHelper;
    }

    public boolean isStringConcatenation(CompilationUnit unit, Expression expr) {
        Predicate<MethodCallExpr> isStringFormatMethod = staticScopeHelper.isFromScope(unit, "format", "String", "java.lang");
        if(expr.isMethodCallExpr() && isStringFormatMethod.test(expr.asMethodCallExpr())) {
            return true;
        }

        return isStringConcatenation(expr);
    }

    private boolean isStringConcatenation(Expression expr) {
        if(expr.isBinaryExpr() && expr.asBinaryExpr().getOperator().asString().equals("+")) {
            return isLiteralStringOrConcatenation(expr);
        }

        return false;
    }

    private boolean isLiteralStringOrConcatenation(Expression expr) {
        if(expr.isBinaryExpr() && expr.asBinaryExpr().getOperator().asString().equals("+")) {
            boolean isLeftArgumentString = isLiteralStringOrConcatenation(expr.asBinaryExpr().getLeft());
            boolean isRightArgumentString = isLiteralStringOrConcatenation(expr.asBinaryExpr().getRight());
            return isLeftArgumentString || isRightArgumentString;
        }

        return expr.isStringLiteralExpr();
    }
}
