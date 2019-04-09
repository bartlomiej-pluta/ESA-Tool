package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.helper.ParentNodeFinder;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;

public class ExternalStoragePlugin extends JavaPlugin {
    private final ParentNodeFinder parentNodeFinder;

    @Inject
    public ExternalStoragePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, ParentNodeFinder parentNodeFinder) {
        super(globMatcher, xmlHelper);
        this.parentNodeFinder = parentNodeFinder;
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.findAll(MethodCallExpr.class).stream()
                .filter(expr -> expr.getName().getIdentifier().matches("getExternalStorageDirectory|getExternalStoragePublicDirectory"))
                .forEach(this::findCheckingStorageStateForAccessingExternalStorage);
    }

    private void findCheckingStorageStateForAccessingExternalStorage(MethodCallExpr accessingToExternalStorage) {
        parentNodeFinder.findParentNode(accessingToExternalStorage, MethodDeclaration.class).ifPresent(methodDeclaration ->
                findCheckingStorageStateInMethodDeclaration(accessingToExternalStorage, methodDeclaration)
        );
    }

    private void findCheckingStorageStateInMethodDeclaration(MethodCallExpr accessingToExternalStorage, MethodDeclaration methodDeclaration) {
        boolean isStateBeingChecked = methodDeclaration.findAll(MethodCallExpr.class).stream()
                .anyMatch(e -> e.getName().getIdentifier().equals("getExternalStorageState"));

        if (!isStateBeingChecked) {
            addIssue(Severity.WARNING, getLineNumberFromExpression(accessingToExternalStorage), accessingToExternalStorage.toString());
        }
    }
}
