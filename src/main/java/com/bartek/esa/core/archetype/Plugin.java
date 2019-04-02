package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.object.Issue;
import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

public interface Plugin {
    boolean supports(File file);
    void update(File file, Document manifest);
    List<Issue> runForIssues();
}
