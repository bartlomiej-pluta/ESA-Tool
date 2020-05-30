package com.bartek.esa.formatter.formatter;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.formatter.archetype.Formatter;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleFormatter implements Formatter {
    private final DescriptionProvider descriptionProvider;

    @Inject
    public SimpleFormatter(DescriptionProvider descriptionProvider) {
        this.descriptionProvider = descriptionProvider;
    }

    @Override
    public void beforeFormat() {
        // nothing to do
    }

    @Override
    public String format(Set<Issue> issues) {
        if (issues.isEmpty()) {
            return "No issues found.";
        }

        String format = issues.stream()
                .sorted()
                .map(this::format)
                .collect(Collectors.joining());

        return String.format("%s\n%s", format.substring(0, format.length() - 2), printSummary(issues));
    }

    @Override
    public void afterFormat() {
        // nothing to do
    }

    private String format(Issue issue) {
        StringBuilder format = new StringBuilder();
        appendDescription(issue, format);
        appendFile(issue, format);
        appendLine(issue, format);
        format.append("\n");

        return format.toString();
    }

    private void appendDescription(Issue issue, StringBuilder format) {
        String description = descriptionProvider.getDescriptionForIssue(issue);
        format
                .append(issue.getSeverity().name())
                .append(": ").append(description)
                .append("\n");
    }

    private void appendFile(Issue issue, StringBuilder format) {
        Optional.ofNullable(issue.getFile())
                .ifPresent(file ->
                        format
                                .append("File: ")
                                .append(file.getAbsolutePath())
                                .append("\n")
                );
    }

    private void appendLine(Issue issue, StringBuilder format) {
        Optional.ofNullable(issue.getLine())
                .ifPresent(line -> {
                    format.append("Line");
                    Optional.ofNullable(issue.getLineNumber()).ifPresentOrElse(
                            number -> format.append(" ").append(number).append(": "),
                            () -> format.append(": ")
                    );
                    format.append(line).append("\n");
                });
    }

    private String printSummary(Set<Issue> issues) {
        StringBuilder format = new StringBuilder();
        format.append("\n--- Total:\n");
        Arrays.stream(Severity.values())
                .forEach(severity -> format
                        .append(severity.name())
                        .append(": ")
                        .append(countIssuesBySeverity(issues, severity))
                        .append("\n"));
        return format.toString();
    }

    private long countIssuesBySeverity(Set<Issue> issues, Severity severity) {
        return issues.stream()
                .map(Issue::getSeverity)
                .filter(severity::equals)
                .count();
    }
}
