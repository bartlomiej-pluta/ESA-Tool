package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.helper.ParentNodeFinder;
import com.bartek.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;
import java.util.function.Consumer;

public class ExternalStoragePlugin extends JavaPlugin {
    private final ParentNodeFinder parentNodeFinder;

    @Inject
    public ExternalStoragePlugin(ParentNodeFinder parentNodeFinder) {
        this.parentNodeFinder = parentNodeFinder;
    }

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getExternalStorageDirectory|getExternalStoragePublicDirectory"))
                .forEach(findCheckingStorageStateForAccessingExternalStorage(java));
    }

    private Consumer<MethodCallExpr> findCheckingStorageStateForAccessingExternalStorage(Source<CompilationUnit> java) {
        return accessingToExternalStorage -> parentNodeFinder
                .findParentNode(accessingToExternalStorage, MethodDeclaration.class)
                .ifPresent(methodDeclaration ->
                        findCheckingStorageStateInMethodDeclaration(java, accessingToExternalStorage, methodDeclaration)
                );
    }

    private void findCheckingStorageStateInMethodDeclaration(Source<CompilationUnit> java, MethodCallExpr accessingToExternalStorage, MethodDeclaration methodDeclaration) {
        boolean isStateBeingChecked = methodDeclaration.findAll(MethodCallExpr.class).stream()
                .anyMatch(e -> e.getName().getIdentifier().equals("getExternalStorageState"));

        if (!isStateBeingChecked) {
            addJavaIssue(Severity.WARNING, java.getFile(), accessingToExternalStorage);
        }
    }
}
