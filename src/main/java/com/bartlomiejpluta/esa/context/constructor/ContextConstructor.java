package com.bartlomiejpluta.esa.context.constructor;

import com.bartlomiejpluta.esa.context.model.Context;
import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.xml.XmlHelper;
import com.bartlomiejpluta.esa.error.EsaException;
import com.bartlomiejpluta.esa.file.matcher.PackageNameMatcher;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.Problem;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

public class ContextConstructor {
    private final XmlHelper xmlHelper;
    private final PackageNameMatcher packageNameMatcher;

    @Inject
    public ContextConstructor(XmlHelper xmlHelper, PackageNameMatcher packageNameMatcher) {
        this.xmlHelper = xmlHelper;
        this.packageNameMatcher = packageNameMatcher;
    }

    public Context construct(File androidManifestFile, Set<File> javaFiles, Set<File> layoutFiles) {
        Document manifest = xmlHelper.parseXml(androidManifestFile);
        String packageName = getPackageName(manifest);

        return Context.builder()
                .packageName(packageName)
                .minSdkVersion(getUsesSdkVersion(manifest, "android:minSdkVersion"))
                .targetSdkVersion(getUsesSdkVersion(manifest, "android:targetSdkVersion"))
                .maxSdkVersion(getUsesSdkVersion(manifest, "android:maxSdkVersion"))
                .manifest(new Source<>(androidManifestFile, manifest))
                .javaSources(parseJavaFiles(javaFiles, packageName))
                .layouts(parseLayoutFiles(layoutFiles))
                .build();
    }

    private String getPackageName(Document androidManifest) {
        return Optional.ofNullable(xmlHelper.xPath(androidManifest, "/manifest", XPathConstants.NODE))
                .map(n -> (Node) n)
                .map(Node::getAttributes)
                .map(attr -> attr.getNamedItem("package"))
                .map(Node::getNodeValue)
                .orElseThrow(() -> new EsaException("No 'package' attribute found in manifest file. Interrupting..."));
    }

    private Integer getUsesSdkVersion(Document manifest, String attribute) {
        return Optional.ofNullable(xmlHelper.xPath(manifest, "/manifest/uses-sdk", XPathConstants.NODE))
                .map(n -> (Node) n)
                .map(Node::getAttributes)
                .map(attr -> attr.getNamedItem(attribute))
                .map(Node::getNodeValue)
                .map(Integer::parseInt)
                .orElse(null);

    }

    private Set<Source<Document>> parseLayoutFiles(Set<File> layoutFiles) {
        return layoutFiles.stream()
                .map(file -> new Source<>(file, xmlHelper.parseXml(file)))
                .collect(toSet());
    }

    private Set<Source<CompilationUnit>> parseJavaFiles(Set<File> javaFiles, String packageName) {
        return javaFiles.stream()
                .filter(file -> packageNameMatcher.doesFileMatchPackageName(file, packageName))
                .map(file -> new Source<>(file, parseJava(file)))
                .filter(s -> s.getModel() != null)
                .collect(toSet());
    }

    private CompilationUnit parseJava(File javaFile) {
        try {
            return StaticJavaParser.parse(javaFile);
        } catch (ParseProblemException e) {
            printParsingErrorToStderr(e, javaFile);
        } catch (Exception e) {
            throw new EsaException(e);
        }

        return null;
    }

    private void printParsingErrorToStderr(ParseProblemException e, File file) {
        e.getProblems().stream()
                .map(p -> format("%s%s:\n%s\nIgnoring file...\n", file.getAbsolutePath(), getProblemRange(p), p.getMessage()))
                .forEach(System.err::println);
    }

    private String getProblemRange(Problem problem) {
        return problem.getLocation()
                .flatMap(TokenRange::toRange)
                .map(range -> format(" (line %d, col %d)", range.begin.line, range.begin.column))
                .orElse("");
    }
}
