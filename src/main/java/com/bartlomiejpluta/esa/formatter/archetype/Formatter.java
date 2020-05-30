package com.bartlomiejpluta.esa.formatter.archetype;

import com.bartlomiejpluta.esa.core.model.object.Issue;

import java.util.Set;

public interface Formatter {
    void beforeFormat();
    String format(Set<Issue> issues);
    void afterFormat();
}
