package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.model.object.Issue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePlugin implements Plugin {
    private List<Issue> issues = new ArrayList<>();
    private File file;

    @Override
    public void update(File file) {
        this.file = file;
        this.issues.clear();
    }

    @Override
    public List<Issue> runForIssues() {
        run(file);
        return issues;
    }

    protected abstract void run(File file);

    protected void addIssue(Severity severity, Integer lineNumber, String line) {
        addIssue(severity, "", lineNumber, line);
    }

    protected void addIssue(Severity severity, String descriptionCode, Integer lineNumber, String line) {
        Issue issue = Issue.builder()
                .severity(severity)
                .issuer(this.getClass())
                .descriptionCode(descriptionCode)
                .file(file)
                .lineNumber(lineNumber)
                .line(line)
                .build();

        issues.add(issue);
    }

    protected File getOriginalFile() {
        return file;
    }
}
