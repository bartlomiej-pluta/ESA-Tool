package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.java.JavaSyntaxRegexProvider;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ImplicitIntentsPlugin extends JavaPlugin {
    private final JavaSyntaxRegexProvider java;

    @Inject
    public ImplicitIntentsPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, JavaSyntaxRegexProvider javaSyntaxRegexProvider) {
        super(globMatcher, xmlHelper);
        this.java = javaSyntaxRegexProvider;
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        List<String> intentVariables = getIntentVariables(compilationUnit);
        findAllSetActionMethodInvocations(compilationUnit, intentVariables);
        compilationUnit.findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("Intent"))
                .filter(this::checkConstructor)
                .forEach(objectCreation -> addIssue(Severity.INFO, getLineNumberFromExpression(objectCreation), objectCreation.toString()));

    }

    private boolean checkConstructor(ObjectCreationExpr objectCreation) {
        NodeList<Expression> arguments = objectCreation.getArguments();
        boolean isImplicit = false;
        if (arguments.size() == 1) {
            Expression argument = arguments.get(0);
            isImplicit = java.isConstant(argument);
        }

        if(arguments.size() == 2) {
            Expression argument = arguments.get(0);
            isImplicit = !argument.isThisExpr();
        }

        return isImplicit;
    }

    private void findAllSetActionMethodInvocations(CompilationUnit compilationUnit, List<String> intentVariables) {
        compilationUnit.findAll(MethodCallExpr.class).forEach(methodCall -> {
            boolean isCalledOnIntentObject = methodCall.getScope()
                    .map(Expression::toString)
                    .filter(intentVariables::contains)
                    .isPresent();
            if(isCalledOnIntentObject && methodCall.getName().getIdentifier().equals("setAction")) {
                addIssue(Severity.INFO, getLineNumberFromExpression(methodCall), methodCall.toString());
            }
        });
    }

    private List<String> getIntentVariables(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(VariableDeclarationExpr.class).stream()
                    .filter(expr -> expr.getElementType().toString().equals("Intent"))
                    .map(VariableDeclarationExpr::getVariables)
                    .flatMap(NodeList::stream)
                    .map(VariableDeclarator::getName)
                    .map(SimpleName::getIdentifier)
                    .collect(Collectors.toList());
    }
}
