package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.JavaPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import javax.inject.Inject;

public class SecureRandomPlugin extends JavaPlugin {

    @Inject
    public SecureRandomPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    public void run(CompilationUnit compilationUnit) {
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(ObjectCreationExpr objectCreation, Void arg) {
                String identifier = objectCreation.getType().getName().getIdentifier();
                NodeList<Expression> arguments = objectCreation.getArguments();

                if(identifier.equals("SecureRandom") && arguments.isNonEmpty()) {
                    addIssue(Severity.VULNERABILITY, getLineNumberFromExpression(objectCreation), objectCreation.toString());
                }

                super.visit(objectCreation, arg);
            }
        }, null);
    }
}
