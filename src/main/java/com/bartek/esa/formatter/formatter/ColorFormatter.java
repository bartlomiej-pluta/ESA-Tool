package com.bartek.esa.formatter.formatter;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.formatter.archetype.Formatter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class ColorFormatter implements Formatter {
    private static final Ansi.Color INFO_COLOR = GREEN;
    private static final Ansi.Color WARNING_COLOR = YELLOW;
    private static final Ansi.Color ERROR_COLOR = MAGENTA;
    private static final Ansi.Color VULNERABILITY_COLOR = RED;

    private final DescriptionProvider descriptionProvider;

    @Inject
    public ColorFormatter(DescriptionProvider descriptionProvider) {
        this.descriptionProvider = descriptionProvider;
    }

    @Override
    public void format(Set<Issue> issues) {
        AnsiConsole.systemInstall();
        if (issues.isEmpty()) {
            Ansi noIssuesFound = ansi().fg(GREEN).a("No issues found.").reset();
            System.out.println(noIssuesFound);
            return;
        }

        String format = issues.stream()
                .sorted()
                .map(this::format)
                .collect(Collectors.joining());

        System.out.println(format.substring(0, format.length() - 2));
        System.out.println(printSummary(issues));
        AnsiConsole.systemUninstall();
    }

    private String format(Issue issue) {
        Ansi ansi = ansi();
        ansi = appendDescription(issue, ansi);
        ansi = appendFile(issue, ansi);
        ansi = appendLine(issue, ansi);
        ansi.a("\n");

        return ansi.toString();
    }

    private Ansi appendDescription(Issue issue, Ansi ansi) {
        String description = descriptionProvider.getDescriptionForIssue(issue);
        String[] lines = description.split("\n");
        String firstLine = lines[0];
        String theRestOfLines = lines.length > 1 ? "\n" + String.join("\n", Arrays.copyOfRange(lines, 1, lines.length)) : "";

        return ansi
                .fg(getColorForSeverity(issue))
                .a(issue.getSeverity().name())
                .reset()
                .a(": ").a(firstLine)
                .fgBrightBlack().a(theRestOfLines)
                .reset()
                .a("\n");
    }

    private Ansi appendFile(Issue issue, Ansi ansi) {
        return Optional.ofNullable(issue.getFile())
                .map(file -> ansi
                        .fg(BLUE)
                        .a("File: ")
                        .fg(CYAN)
                        .a(file.getAbsolutePath())
                        .reset()
                        .a("\n"))
                .orElse(ansi);
    }

    private Ansi appendLine(Issue issue, Ansi ansi) {
        Optional.ofNullable(issue.getLine())
                .ifPresent(line -> {
                    ansi
                            .fg(CYAN)
                            .a("Line");
                    Optional.ofNullable(issue.getLineNumber()).ifPresentOrElse(
                            number -> ansi.a(" ").fg(MAGENTA).a(number).fg(CYAN).a(": "),
                            () -> ansi.a(": ")
                    );
                    ansi.fg(BLUE).a(line).reset().a("\n");
                });
        return ansi;
    }

    private String printSummary(Set<Issue> issues) {
        Ansi ansi = ansi();
        ansi.a("\n--- Total:\n");
        Arrays.stream(Severity.values())
                .forEach(severity -> ansi.fg(getColorForSeverity(severity))
                        .a(severity.name())
                        .a(": ")
                        .reset()
                        .a(countIssuesBySeverity(issues, severity))
                        .a("\n"));
        return ansi.toString();
    }

    private long countIssuesBySeverity(Set<Issue> issues, Severity severity) {
        return issues.stream()
                .map(Issue::getSeverity)
                .filter(severity::equals)
                .count();
    }

    private Ansi.Color getColorForSeverity(Issue issue) {
        return getColorForSeverity(issue.getSeverity());
    }

    private Ansi.Color getColorForSeverity(Severity severity) {
        switch (severity) {
            case INFO:
                return GREEN;
            case WARNING:
                return YELLOW;
            case ERROR:
                return MAGENTA;
            case VULNERABILITY:
                return RED;
        }

        return RED;
    }
}
