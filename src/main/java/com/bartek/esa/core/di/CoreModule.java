package com.bartek.esa.core.di;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.core.executor.PluginExecutor;
import com.bartek.esa.core.helper.ParentNodeFinder;
import com.bartek.esa.core.helper.StaticScopeHelper;
import com.bartek.esa.core.helper.StringConcatenationChecker;
import com.bartek.esa.core.java.JavaSyntaxRegexProvider;
import com.bartek.esa.core.xml.XmlHelper;
import dagger.Module;
import dagger.Provides;

@Module
public class CoreModule {

    @Provides
    public PluginExecutor pluginExecutor() {
        return new PluginExecutor();
    }

    @Provides
    public DescriptionProvider descriptionProvider() {
        return new DescriptionProvider();
    }

    @Provides
    public JavaSyntaxRegexProvider javaSyntaxRegexProvider() {
        return new JavaSyntaxRegexProvider();
    }

    @Provides
    public XmlHelper xmlHelper() {
        return new XmlHelper();
    }

    @Provides
    public StaticScopeHelper staticScopeHelper() {
        return new StaticScopeHelper();
    }

    @Provides
    public StringConcatenationChecker stringConcatenationChecker(StaticScopeHelper staticScopeHelper) {
        return new StringConcatenationChecker(staticScopeHelper);
    }

    @Provides
    public ParentNodeFinder parentNodeFinder() {
        return new ParentNodeFinder();
    }
}
