package com.bartek.esa.core.java;

import javax.inject.Inject;

import static java.lang.String.format;

public class JavaSyntaxRegexProvider {

    @Inject
    public JavaSyntaxRegexProvider() {

    }

    public String methodInvocation(String methodName) {
        return format("^%s\\s*\\($", methodName);
    }
}
