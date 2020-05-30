package com.bartlomiejpluta.esa.core.java;

import com.github.javaparser.ast.expr.Expression;

import javax.inject.Inject;
import java.util.regex.Pattern;

public class JavaSyntaxRegexProvider {

    @Inject
    public JavaSyntaxRegexProvider() {

    }

    public Pattern constant() {
        return Pattern.compile("^[A-Z0-9_$]*$");
    }

    public boolean isConstant(Expression expression) {
        String value = expression.toString();
        if(expression.isNameExpr() && constant().matcher(value).matches()) {
            return true;
        }

        if(expression.isFieldAccessExpr()) {
            return constant().matcher(expression.asFieldAccessExpr().getName().getIdentifier()).matches();
        }

        return false;
    }
}
