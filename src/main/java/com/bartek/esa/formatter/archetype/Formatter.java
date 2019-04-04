package com.bartek.esa.formatter.archetype;

import com.bartek.esa.core.model.object.Issue;

import java.util.List;

public interface Formatter {
    void format(List<Issue> issues);
}
