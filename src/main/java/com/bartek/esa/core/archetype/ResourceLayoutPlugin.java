package com.bartek.esa.core.archetype;

import com.bartek.esa.file.matcher.GlobMatcher;

import java.io.File;

public abstract class ResourceLayoutPlugin extends XmlPlugin {
    private final GlobMatcher globMatcher;

    public ResourceLayoutPlugin(GlobMatcher globMatcher) {
        super(globMatcher);
        this.globMatcher = globMatcher;
    }

    @Override
    public boolean supports(File file) {
        return globMatcher.fileMatchesGlobPattern(file, "**/res/layout*/*.xml");
    }
}
