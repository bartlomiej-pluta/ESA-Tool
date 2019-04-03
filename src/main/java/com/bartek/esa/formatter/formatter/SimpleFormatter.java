package com.bartek.esa.formatter.formatter;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.formatter.archetype.Formatter;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimpleFormatter implements Formatter {
    private final DescriptionProvider descriptionProvider;

    @Inject
    public SimpleFormatter(DescriptionProvider descriptionProvider) {
        this.descriptionProvider = descriptionProvider;
    }

    @Override
    public void format(List<Issue> issues) {
        String format = issues.stream()
                .map(this::format)
                .collect(Collectors.joining());

        System.out.println(format.substring(0, format.length() - 2));
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
        format
                .append("File: ")
                .append(issue.getFile().getAbsolutePath())
                .append("\n");
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
}
