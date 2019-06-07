package com.bartek.esa.formatter.archetype;

import com.bartek.esa.core.model.object.Issue;

import java.util.Set;

public interface Formatter {
    void beforeFormat();
    String format(Set<Issue> issues);
    void afterFormat();
}
