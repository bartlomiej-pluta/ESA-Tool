package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.Issue;

import java.io.File;
import java.util.List;

public interface Plugin {
    boolean supports(File file);
    void update(File file);
    List<Issue> runForIssues();
}
