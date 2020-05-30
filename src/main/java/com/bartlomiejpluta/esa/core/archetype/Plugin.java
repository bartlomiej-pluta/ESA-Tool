package com.bartlomiejpluta.esa.core.archetype;

import com.bartlomiejpluta.esa.context.model.Context;
import com.bartlomiejpluta.esa.core.model.object.Issue;

import java.util.Set;

public interface Plugin {
    Set<Issue> runForIssues(Context context);
}
