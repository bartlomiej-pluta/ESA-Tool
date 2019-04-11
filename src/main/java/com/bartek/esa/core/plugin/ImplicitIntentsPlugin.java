package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.java.JavaSyntaxRegexProvider;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
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
        checkCreatingImplicitIntents(compilationUnit);
        checkCreatingPendingIntentsWithoutIntentVariable(compilationUnit);
        checkCreatingPendingIntentsWithIntentVariables(compilationUnit);
        checkCreatingPendingIntentsWithIntentsArraysVariables(compilationUnit);
    }

    // Works for:
    // Intent[] myIntents = { new Intent(...), ... }
    // getActivities(this, 0, myIntents, 0);
    private void checkCreatingPendingIntentsWithIntentsArraysVariables(CompilationUnit compilationUnit) {
        List<String> implicitIntentsArraysVariables = compilationUnit.findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("Intent"))
                .filter(this::isCreatingImplicitIntent)
                .map(Node::getParentNode)
                .flatMap(Optional::stream)
                .map(Node::getParentNode)
                .flatMap(Optional::stream)
                .filter(node -> node instanceof VariableDeclarator)
                .map(node -> (VariableDeclarator) node)
                .map(VariableDeclarator::getName)
                .map(SimpleName::getIdentifier)
                .collect(Collectors.toList());
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivities"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isNameExpr())
                .filter(expr -> implicitIntentsArraysVariables.contains(expr.getArguments().get(2).asNameExpr().getName().getIdentifier()))
                .forEach(expr -> addIssue(Severity.VULNERABILITY, ".PENDING_INTENT", getLineNumberFromExpression(expr), expr.toString()));
    }

    private void checkCreatingImplicitIntents(CompilationUnit compilationUnit) {
        List<String> intentVariables = getIntentVariables(compilationUnit);
        checkAllSetActionMethodInvocations(compilationUnit, intentVariables);
        checkCreatingImplicitIntentsUsingConstructor(compilationUnit);
    }

    // Works for: new Intent(Intent.ABC), new Intent(ABC)
    private void checkCreatingImplicitIntentsUsingConstructor(CompilationUnit compilationUnit) {
        compilationUnit.findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("Intent"))
                .filter(this::isCreatingImplicitIntent)
                .forEach(objectCreation -> addIssue(Severity.INFO, ".IMPLICIT_INTENT", getLineNumberFromExpression(objectCreation), objectCreation.toString()));
    }

    // Returns: i for: Intent i = new Intent(...)
    private List<String> getIntentVariables(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(VariableDeclarationExpr.class).stream()
                .filter(expr -> expr.getElementType().toString().equals("Intent"))
                .map(VariableDeclarationExpr::getVariables)
                .flatMap(NodeList::stream)
                .map(VariableDeclarator::getName)
                .map(SimpleName::getIdentifier)
                .collect(Collectors.toList());
    }

    // Checks if: new Intent(Intent.ABC), new Intent(ABC)
    private boolean isCreatingImplicitIntent(ObjectCreationExpr objectCreation) {
        NodeList<Expression> arguments = objectCreation.getArguments();
        boolean isImplicit = false;

        // Works for: new Intent(CONSTANT, ...)
        if (arguments.size() == 1) {
            Expression argument = arguments.get(0);
            isImplicit = java.isConstant(argument);
        }

        // Not works for: new Intent(this, ...)
        if(arguments.size() == 2) {
            Expression firstArg = arguments.get(0);
            Expression secondArg = arguments.get(1);
            boolean isThisExpr = firstArg.isThisExpr();
            boolean isTryingToGetClass = secondArg.isClassExpr();
            boolean isExplicit = isThisExpr || isTryingToGetClass;
            isImplicit = !isExplicit;
        }

        return isImplicit;
    }

    // Works for: i.setAction(...)
    private void checkAllSetActionMethodInvocations(CompilationUnit compilationUnit, List<String> intentVariables) {
        compilationUnit.findAll(MethodCallExpr.class).forEach(methodCall -> {
            boolean isCalledOnIntentObject = methodCall.getScope()
                    .map(Expression::toString)
                    .filter(intentVariables::contains)
                    .isPresent();
            if(isCalledOnIntentObject && methodCall.getName().getIdentifier().equals("setAction")) {
                addIssue(Severity.INFO, ".IMPLICIT_INTENT", getLineNumberFromExpression(methodCall), methodCall.toString());
            }
        });
    }

    // Works for:
    // Intent myIntent = new Intent(...)
    // getActivity(this, 0, myIntent, 0);
    private void checkCreatingPendingIntentsWithIntentVariables(CompilationUnit compilationUnit) {
        List<String> implicitIntentsVariables = compilationUnit.findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("Intent"))
                .filter(this::isCreatingImplicitIntent)
                .map(Node::getParentNode)
                .flatMap(Optional::stream)
                .filter(node -> node instanceof VariableDeclarator)
                .map(node -> (VariableDeclarator) node)
                .map(VariableDeclarator::getName)
                .map(SimpleName::getIdentifier)
                .collect(Collectors.toList());
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivity|getBroadcast|getService"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isNameExpr())
                .filter(expr -> implicitIntentsVariables.contains(expr.getArguments().get(2).asNameExpr().getName().getIdentifier()))
                .forEach(expr -> addIssue(Severity.VULNERABILITY, ".PENDING_INTENT", getLineNumberFromExpression(expr), expr.toString()));
    }

    private void checkCreatingPendingIntentsWithoutIntentVariable(CompilationUnit compilationUnit) {
        // Works for: getActivity(this, 0, new Intent(...), 0)
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivity|getBroadcast|getService"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isObjectCreationExpr())
                .filter(expr -> isCreatingImplicitIntent(expr.getArguments().get(2).asObjectCreationExpr()))
                .forEach(expr -> addIssue(Severity.VULNERABILITY, ".PENDING_INTENT", getLineNumberFromExpression(expr), expr.toString()));

        // Works for: getActivities(this, 0, new Intent[] { new Intent(...), ...}, 0)
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivities"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isArrayCreationExpr())
                .filter(expr -> isCreatingImplicitIntentsArray(expr.getArguments().get(2).asArrayCreationExpr()))
                .forEach(expr -> addIssue(Severity.VULNERABILITY, ".PENDING_INTENT", getLineNumberFromExpression(expr), expr.toString()));
    }

    private boolean isCreatingImplicitIntentsArray(ArrayCreationExpr arrayCreationExpr) {
        return arrayCreationExpr.getInitializer()
                .map(this::isCreatingImplicitIntentsArray)
                .orElse(false);
    }

    private boolean isCreatingImplicitIntentsArray(ArrayInitializerExpr arrayInitializerExpr) {
        return arrayInitializerExpr.getValues().stream()
                .filter(Expression::isObjectCreationExpr)
                .map(Expression::asObjectCreationExpr)
                .anyMatch(this::isCreatingImplicitIntent);
    }
}
