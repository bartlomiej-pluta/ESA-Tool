package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.JavaPlugin;
import com.bartlomiejpluta.esa.core.helper.ParentNodeFinder;
import com.bartlomiejpluta.esa.core.helper.StaticScopeHelper;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import javax.inject.Inject;
import java.util.function.Consumer;

import static com.bartlomiejpluta.esa.core.helper.NodeUtil.is;

public class ExternalStoragePlugin extends JavaPlugin {
    private final ParentNodeFinder parentNodeFinder;
    private final StaticScopeHelper staticScopeHelper;

    @Inject
    public ExternalStoragePlugin(ParentNodeFinder parentNodeFinder, StaticScopeHelper staticScopeHelper) {
        this.parentNodeFinder = parentNodeFinder;
        this.staticScopeHelper = staticScopeHelper;
    }

    @Override
    public void run(Source<CompilationUnit> java) {
        java.getModel().findAll(MethodCallExpr.class).stream()
                .filter(staticScopeHelper.isFromScope(java.getModel(), "getExternalStorageDirectory|getExternalStoragePublicDirectory", "Environment", "android.os"))
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
                .filter(staticScopeHelper.isFromScope(java.getModel(), "getExternalStorageState", "Environment", "android.os"))
                .anyMatch(checkingMethod -> is(accessingToExternalStorage).after(checkingMethod));

        if (!isStateBeingChecked) {
            addJavaIssue(Severity.WARNING, java.getFile(), accessingToExternalStorage);
        }
    }
}
