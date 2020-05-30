package com.bartlomiejpluta.esa.core.archetype;

import com.bartlomiejpluta.esa.context.model.Context;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.bartlomiejpluta.esa.core.model.object.Issue;
import com.github.javaparser.ast.expr.Expression;
import org.w3c.dom.Node;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

public abstract class BasePlugin implements Plugin {
    private Set<Issue> issues;

    @Override
    public Set<Issue> runForIssues(Context context) {
        issues = new HashSet<>();
        run(context);
        return issues;
    }

    protected abstract void run(Context context);

    protected void addJavaIssue(Severity severity, File file, Expression expression) {
        addIssue(severity, file, getLineNumberFromExpression(expression), expression.toString());
    }

    private Integer getLineNumberFromExpression(Expression expression) {
        return expression.getRange().map(r -> r.begin.line).orElse(null);
    }

    protected void addJavaIssue(Severity severity, Map<String, String> descriptionModel, File file, Expression expression) {
        addIssue(severity, "", descriptionModel, file, getLineNumberFromExpression(expression), expression.toString());
    }


    protected void addJavaIssue(Severity severity, String descriptionCode, File file, Expression expression) {
        addIssue(severity, descriptionCode, new HashMap<>(), file, getLineNumberFromExpression(expression), expression.toString());
    }

    protected void addJavaIssue(Severity severity, String descriptionCode, Map<String, String> descriptionModel, File file, Expression expression) {
        addIssue(severity, descriptionCode, descriptionModel, file, getLineNumberFromExpression(expression), expression.toString());
    }

    protected void addXmlIssue(Severity severity, File file, Node node) {
        addIssue(severity, file, null, tagString(node));
    }

    protected void addXmlIssue(Severity severity, Map<String, String> descriptionModel, File file, Node node) {
        addIssue(severity, descriptionModel, file, null, tagString(node));
    }

    protected void addXmlIssue(Severity severity, String descriptionCode, File file, Node node) {
        addIssue(severity, descriptionCode, file, null, tagString(node));
    }

    protected void addXmlIssue(Severity severity, String descriptionCode, Map<String, String> descriptionModel, File file, Node node) {
        addIssue(severity, descriptionCode, descriptionModel, file, null, tagString(node));
    }

    protected String tagString(Node node) {
        Node[] attributes = new Node[node.getAttributes().getLength()];
        for (int i = 0; i < attributes.length; ++i) {
            attributes[i] = node.getAttributes().item(i);
        }

        String attributesString = Arrays.stream(attributes)
                .map(n -> format("%s=\"%s\"", n.getNodeName(), n.getNodeValue()))
                .collect(Collectors.joining(" "));

        return format("<%s %s ...", node.getNodeName(), attributesString);
    }

    protected void addIssue(Severity severity, File file, Integer lineNumber, String line) {
        addIssue(severity, "", file, lineNumber, line);
    }

    protected void addIssue(Severity severity, Map<String, String> descriptionModel, File file, Integer lineNumber, String line) {
        addIssue(severity, "", descriptionModel, file, lineNumber, line);
    }

    protected void addIssue(Severity severity, String descriptionCode, File file, Integer lineNumber, String line) {
        addIssue(severity, descriptionCode, new HashMap<>(), file, lineNumber, line);
    }

    protected void addIssue(Severity severity, String descriptionCode, Map<String, String> descriptionModel, File file, Integer lineNumber, String line) {
        Issue issue = Issue.builder()
                .severity(severity)
                .issuer(this.getClass())
                .descriptionCode(descriptionCode)
                .descriptionModel(descriptionModel)
                .file(file)
                .lineNumber(lineNumber)
                .line(line)
                .build();

        addIssue(issue);
    }

    protected void addIssue(Issue issue) {
        issues.add(issue);
    }
}
