package com.bartek.esa.formatter.archetype;

import com.bartek.esa.core.model.object.Issue;

import java.util.Set;

public interface Formatter {
    void format(Set<Issue> issues);
}
