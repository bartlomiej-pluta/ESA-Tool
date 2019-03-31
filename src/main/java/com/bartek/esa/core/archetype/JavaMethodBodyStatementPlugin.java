package com.bartek.esa.core.archetype;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class JavaMethodBodyStatementPlugin extends JavaPlugin {

    @Override
    public void run(CompilationUnit compilationUnit) {
        Stream<Statement> statements = compilationUnit.getTypes().stream()
                .map(TypeDeclaration::getMembers)
                .flatMap(Collection::stream)
                .filter(BodyDeclaration::isMethodDeclaration)
                .map(b -> (MethodDeclaration) b)
                .map(MethodDeclaration::getBody)
                .flatMap(Optional::stream)
                .map(BlockStmt::getStatements)
                .flatMap(Collection::stream);

        checkStatements(statements);
    }

    protected abstract void checkStatements(Stream<Statement> statements);

    protected Integer getLineNumberOfStatement(Statement statement) {
        return statement.getRange()
                .map(range -> range.begin)
                .map(begin -> begin.line)
                .orElse(null);
    }
}
