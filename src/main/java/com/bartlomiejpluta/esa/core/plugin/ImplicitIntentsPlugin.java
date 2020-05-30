package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.JavaPlugin;
import com.bartlomiejpluta.esa.core.java.JavaSyntaxRegexProvider;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
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
    private final JavaSyntaxRegexProvider javaSyntax;

    @Inject
    public ImplicitIntentsPlugin(JavaSyntaxRegexProvider javaSyntaxRegexProvider) {
        this.javaSyntax = javaSyntaxRegexProvider;
    }

    @Override
    public void run(Source<CompilationUnit> java) {
        checkCreatingImplicitIntents(java);
        checkCreatingPendingIntentsWithoutIntentVariable(java);
        checkCreatingPendingIntentsWithIntentVariables(java);
        checkCreatingPendingIntentsWithIntentsArraysVariables(java);
    }

    // Works for:
    // Intent[] myIntents = { new Intent(...), ... }
    // getActivities(this, 0, myIntents, 0);
    private void checkCreatingPendingIntentsWithIntentsArraysVariables(Source<CompilationUnit> java) {
        List<String> implicitIntentsArraysVariables = java.getModel().findAll(ObjectCreationExpr.class).stream()
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
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivities"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isNameExpr())
                .filter(expr -> implicitIntentsArraysVariables.contains(expr.getArguments().get(2).asNameExpr().getName().getIdentifier()))
                .forEach(expr -> addJavaIssue(Severity.VULNERABILITY, ".PENDING_INTENT", java.getFile(), expr));
    }

    private void checkCreatingImplicitIntents(Source<CompilationUnit> java) {
        List<String> intentVariables = getIntentVariables(java);
        checkAllSetActionMethodInvocations(java, intentVariables);
        checkCreatingImplicitIntentsUsingConstructor(java);
    }

    // Works for: new Intent(Intent.ABC), new Intent(ABC)
    private void checkCreatingImplicitIntentsUsingConstructor(Source<CompilationUnit> java) {
        java.getModel().findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("Intent"))
                .filter(this::isCreatingImplicitIntent)
                .forEach(objectCreation -> addJavaIssue(Severity.INFO, ".IMPLICIT_INTENT", java.getFile(), objectCreation));
    }

    // Returns: i for: Intent i = new Intent(...)
    private List<String> getIntentVariables(Source<CompilationUnit> java) {
        return java.getModel().findAll(VariableDeclarationExpr.class).stream()
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
            isImplicit = javaSyntax.isConstant(argument);
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
    private void checkAllSetActionMethodInvocations(Source<CompilationUnit> java, List<String> intentVariables) {
        java.getModel().findAll(MethodCallExpr.class).forEach(methodCall -> {
            boolean isCalledOnIntentObject = methodCall.getScope()
                    .map(Expression::toString)
                    .filter(intentVariables::contains)
                    .isPresent();
            if(isCalledOnIntentObject && methodCall.getName().getIdentifier().equals("setAction")) {
                addJavaIssue(Severity.INFO, ".IMPLICIT_INTENT", java.getFile(), methodCall);
            }
        });
    }

    // Works for:
    // Intent myIntent = new Intent(...)
    // getActivity(this, 0, myIntent, 0);
    private void checkCreatingPendingIntentsWithIntentVariables(Source<CompilationUnit> java) {
        List<String> implicitIntentsVariables = java.getModel().findAll(ObjectCreationExpr.class).stream()
                .filter(expr -> expr.getType().getName().getIdentifier().equals("Intent"))
                .filter(this::isCreatingImplicitIntent)
                .map(Node::getParentNode)
                .flatMap(Optional::stream)
                .filter(node -> node instanceof VariableDeclarator)
                .map(node -> (VariableDeclarator) node)
                .map(VariableDeclarator::getName)
                .map(SimpleName::getIdentifier)
                .collect(Collectors.toList());
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivity|getBroadcast|getService"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isNameExpr())
                .filter(expr -> implicitIntentsVariables.contains(expr.getArguments().get(2).asNameExpr().getName().getIdentifier()))
                .forEach(expr -> addJavaIssue(Severity.VULNERABILITY, ".PENDING_INTENT", java.getFile(), expr));
    }

    private void checkCreatingPendingIntentsWithoutIntentVariable(Source<CompilationUnit> java) {
        // Works for: getActivity(this, 0, new Intent(...), 0)
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivity|getBroadcast|getService"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isObjectCreationExpr())
                .filter(expr -> isCreatingImplicitIntent(expr.getArguments().get(2).asObjectCreationExpr()))
                .forEach(expr -> addJavaIssue(Severity.VULNERABILITY, ".PENDING_INTENT", java.getFile(), expr));

        // Works for: getActivities(this, 0, new Intent[] { new Intent(...), ...}, 0)
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getActivities"))
                .filter(expr -> expr.getArguments().size() >= 4)
                .filter(expr -> expr.getArguments().get(2).isArrayCreationExpr())
                .filter(expr -> isCreatingImplicitIntentsArray(expr.getArguments().get(2).asArrayCreationExpr()))
                .forEach(expr -> addJavaIssue(Severity.VULNERABILITY, ".PENDING_INTENT", java.getFile(), expr));
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
