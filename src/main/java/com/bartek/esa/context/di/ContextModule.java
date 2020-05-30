package com.bartek.esa.context.di;

import com.bartek.esa.context.constructor.ContextConstructor;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.PackageNameMatcher;
import dagger.Module;

@Module
public class ContextModule {

    public ContextConstructor contextConstructor(XmlHelper xmlHelper, PackageNameMatcher packageNameMatcher) {
        return new ContextConstructor(xmlHelper, packageNameMatcher);
    }

}
