package com.bartek.esa.core.di;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.core.java.JavaSyntaxRegexProvider;
import com.bartek.esa.core.xml.XmlHelper;
import dagger.Module;
import dagger.Provides;

@Module
public class CoreModule {

    @Provides
    public PluginExecutor pluginExecutor(XmlHelper xmlHelper) {
        return new PluginExecutor(xmlHelper);
    }

    @Provides
    public JavaSyntaxRegexProvider javaSyntaxRegexProvider() {
        return new JavaSyntaxRegexProvider();
    }

    @Provides
    public DescriptionProvider descriptionProvider() {
        return new DescriptionProvider();
    }

    @Provides
    public XmlHelper xmlHelper() {
        return new XmlHelper();
    }
}
