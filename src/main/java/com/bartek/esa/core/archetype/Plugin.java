package com.bartek.esa.core.archetype;

import com.bartek.esa.context.model.Context;
import com.bartek.esa.core.model.object.Issue;

import java.util.Set;

public interface Plugin {
    Set<Issue> runForIssues(Context context);
}
