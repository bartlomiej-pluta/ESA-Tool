package com.bartek.esa.formatter.formatter;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.formatter.archetype.Formatter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class ColorFormatter implements Formatter {

    private final DescriptionProvider descriptionProvider;

    @Inject
    public ColorFormatter(DescriptionProvider descriptionProvider) {
        this.descriptionProvider = descriptionProvider;
    }

    @Override
    public void format(Set<Issue> issues) {
        AnsiConsole.systemInstall();
        if(issues.isEmpty()) {
            Ansi noIssuesFound = ansi().fg(GREEN).a("No issues found.").reset();
            System.out.println(noIssuesFound);
            return;
        }

        String format = issues.stream()
                .map(this::format)
                .collect(Collectors.joining());

        System.out.println(format.substring(0, format.length() - 2));
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

        return ansi
                .fg(getColorForSeverity(issue))
                .a(issue.getSeverity().name())
                .reset()
                .a(": ").a(description)
                .a("\n");
    }

    private Ansi appendFile(Issue issue, Ansi ansi) {
        return ansi
                .fg(GREEN)
                .a("File: ")
                .reset()
                .a(issue.getFile().getAbsolutePath())
                .a("\n");
    }

    private Ansi appendLine(Issue issue, Ansi ansi) {
        Optional.ofNullable(issue.getLine())
                .ifPresent(line -> {
                    ansi
                            .fg(BLUE)
                            .a("Line");
                    Optional.ofNullable(issue.getLineNumber()).ifPresentOrElse(
                            number -> ansi.a(" ").a(number).a(": "),
                            () -> ansi.a(": ")
                    );
                    ansi.reset().a(line).a("\n");
                });
        return ansi;
    }

    private Ansi.Color getColorForSeverity(Issue issue) {
        switch(issue.getSeverity()) {
            case INFO: return GREEN;
            case WARNING: return YELLOW;
            case ERROR: return MAGENTA;
            case VULNERABILITY: return RED;
        }

        return RED;
    }
}
