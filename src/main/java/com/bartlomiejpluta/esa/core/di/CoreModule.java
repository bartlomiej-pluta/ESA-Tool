package com.bartlomiejpluta.esa.core.di;

import com.bartlomiejpluta.esa.core.desc.provider.DescriptionProvider;
import com.bartlomiejpluta.esa.core.executor.PluginExecutor;
import com.bartlomiejpluta.esa.core.helper.ParentNodeFinder;
import com.bartlomiejpluta.esa.core.helper.StaticScopeHelper;
import com.bartlomiejpluta.esa.core.helper.StringConcatenationChecker;
import com.bartlomiejpluta.esa.core.java.JavaSyntaxRegexProvider;
import com.bartlomiejpluta.esa.core.xml.XmlHelper;
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
