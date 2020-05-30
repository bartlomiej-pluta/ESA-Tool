package com.bartlomiejpluta.esa.context.di;

import com.bartlomiejpluta.esa.context.constructor.ContextConstructor;
import com.bartlomiejpluta.esa.core.xml.XmlHelper;
import com.bartlomiejpluta.esa.file.matcher.PackageNameMatcher;
import dagger.Module;

@Module
public class ContextModule {

    public ContextConstructor contextConstructor(XmlHelper xmlHelper, PackageNameMatcher packageNameMatcher) {
        return new ContextConstructor(xmlHelper, packageNameMatcher);
    }

}
