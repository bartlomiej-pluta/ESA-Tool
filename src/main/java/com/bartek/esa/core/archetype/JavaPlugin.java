package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.util.HashMap;

import static java.lang.String.format;

public abstract class JavaPlugin extends BasePlugin {
    private final GlobMatcher globMatcher;
    private final XmlHelper xmlHelper;

    public JavaPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        this.globMatcher = globMatcher;
        this.xmlHelper = xmlHelper;
    }

    @Override
    public boolean supports(File file) {
        return globMatcher.fileMatchesGlobPattern(file, "**/*.java");
    }

    @Override
    protected void run(File file) {
        if(!isApplicationPackageFile(file)) {
            return;
        }

        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(file);
            run(compilationUnit);
        } catch (ParseProblemException e) {
            printParsingErrorToStderr(e, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printParsingErrorToStderr(ParseProblemException e, File file) {
        e.getProblems().stream()
                .map(p -> format("%s%s:\n%s\nIgnoring file...\n", file.getAbsolutePath(), getRange(p), p.getMessage()))
                .forEach(System.err::println);
    }

    private String getRange(Problem problem) {
        return problem.getLocation()
                .flatMap(TokenRange::toRange)
                .map(range -> format(" (line %d, col %d)", range.begin.line, range.begin.column))
                .orElse("");
    }

    private boolean isApplicationPackageFile(File file) {
        Document manifest = getManifest();
        Node root = (Node) xmlHelper.xPath(manifest, "/manifest", XPathConstants.NODE);
        Node packageValue = root.getAttributes().getNamedItem("package");

        if(packageValue == null) {
            Issue issue = Issue.builder()
                    .issuer(JavaPlugin.class)
                    .descriptionCode(".NO_PACKAGE")
                    .descriptionModel(new HashMap<>())
                    .severity(Severity.ERROR)
                    .build();

            addIssue(issue);

            return false;
        }

        String path = packageValue.getNodeValue().replaceAll("\\.", "/");
        return globMatcher.fileMatchesGlobPattern(file, format("**/%s/**", path));
    }

    protected Integer getLineNumberFromExpression(Expression expression) {
        return expression.getRange().map(r -> r.begin.line).orElse(null);
    }

    public abstract void run(CompilationUnit compilationUnit);
}
