package com.bartek.esa.core.di;

import com.bartek.esa.core.archetype.Plugin;
import com.bartek.esa.core.helper.ParentNodeFinder;
import com.bartek.esa.core.helper.StaticScopeHelper;
import com.bartek.esa.core.java.JavaSyntaxRegexProvider;
import com.bartek.esa.core.plugin.*;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ElementsIntoSet;
import dagger.multibindings.IntoSet;

import java.util.HashSet;
import java.util.Set;

@Module
public class PluginModule {

    @Provides
    @ElementsIntoSet
    public Set<Plugin> plugins() {
        return new HashSet<>();
    }

    @Provides
    @IntoSet
    public Plugin loggingPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, StaticScopeHelper staticScopeHelper) {
        return new LoggingPlugin(globMatcher, xmlHelper, staticScopeHelper);
    }

    @Provides
    @IntoSet
    public Plugin debuggablePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new DebuggablePlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin allowBackupPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new AllowBackupPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin permissionRaceConditionPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new PermissionsRaceConditionPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin secureRandomPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new SecureRandomPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin implicitIntentsPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, JavaSyntaxRegexProvider javaSyntaxRegexProvider) {
        return new ImplicitIntentsPlugin(globMatcher, xmlHelper, javaSyntaxRegexProvider);
    }

    @Provides
    @IntoSet
    public Plugin sharedUidPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new SharedUidPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin usesSdkPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new UsesSdkPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin cipherInstancePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, StaticScopeHelper staticScopeHelper) {
        return new CipherInstancePlugin(globMatcher, xmlHelper, staticScopeHelper);
    }

    @Provides
    @IntoSet
    public Plugin strictModePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, StaticScopeHelper staticScopeHelper) {
        return new StrictModePlugin(globMatcher, xmlHelper, staticScopeHelper);
    }

    @Provides
    @IntoSet
    public Plugin externalStoragePlugin(GlobMatcher globMatcher, XmlHelper xmlHelper, ParentNodeFinder parentNodeFinder) {
        return new ExternalStoragePlugin(globMatcher, xmlHelper, parentNodeFinder);
    }

    @Provides
    @IntoSet
    public Plugin suppressWarningsPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new SuppressWarningsPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin exportedComponentsPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new ExportedComponentsPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin dangerousPermissionPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new DangerousPermissionPlugin(globMatcher, xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin textInputValidationPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        return new TextInputValidationPlugin(globMatcher, xmlHelper);
    }
}
