package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.object.Issue;
import org.w3c.dom.Document;

import java.io.File;
import java.util.Set;

public interface Plugin {
    boolean supports(File file);
    void update(File file, Document manifest);
    Set<Issue> runForIssues();
}
