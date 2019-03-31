package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.Issue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePlugin {
    private List<Issue> issues = new ArrayList<>();
    private File file;

    public void update(File file) {
        this.file = file;
    }

    public List<Issue> runForIssues() {
        run(file);
        return issues;
    }

    protected abstract void run(File file);

    protected void addIssue(int lineNumber, String line) {
        Issue issue = Issue.builder()
                .issuer(this.getClass())
                .file(file)
                .lineNumber(lineNumber)
                .line(line)
                .build();

        issues.add(issue);
    }
}
