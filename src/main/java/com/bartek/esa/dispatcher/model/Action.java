package com.bartek.esa.dispatcher.model;

import com.bartek.esa.core.model.object.Issue;

import java.util.Set;

@FunctionalInterface
public interface Action {

    Set<Issue> perform(String source, Set<String> plugins, Set<String> excludes, boolean debug);
}
