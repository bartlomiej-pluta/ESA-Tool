package com.bartek.esa.context.di;

import com.bartek.esa.context.constructor.ContextConstructor;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import dagger.Module;

@Module
public class ContextModule {

    public ContextConstructor contextConstructor(XmlHelper xmlHelper, GlobMatcher globMatcher) {
        return new ContextConstructor(xmlHelper, globMatcher);
    }

}
