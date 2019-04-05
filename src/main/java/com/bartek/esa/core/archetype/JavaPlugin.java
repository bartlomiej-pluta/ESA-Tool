package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.error.EsaException;
import com.bartek.esa.file.matcher.GlobMatcher;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import io.vavr.control.Try;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.util.HashMap;

public abstract class JavaPlugin extends BasePlugin {
    private final GlobMatcher globMatcher;
    private final XmlHelper xmlHelper;

    public JavaPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(xmlHelper);
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

        CompilationUnit compilationUnit = Try.of(() -> StaticJavaParser.parse(file)).getOrElseThrow(EsaException::new);
        run(compilationUnit);
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
        return globMatcher.fileMatchesGlobPattern(file, String.format("**/%s/**", path));
    }

    protected Integer getLineNumberFromExpression(Expression expression) {
        return expression.getRange().map(r -> r.begin.line).orElse(null);
    }

    public abstract void run(CompilationUnit compilationUnit);
}
