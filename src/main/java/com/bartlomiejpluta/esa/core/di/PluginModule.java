package com.bartlomiejpluta.esa.core.di;

import com.bartlomiejpluta.esa.core.archetype.Plugin;
import com.bartlomiejpluta.esa.core.helper.ParentNodeFinder;
import com.bartlomiejpluta.esa.core.helper.StaticScopeHelper;
import com.bartlomiejpluta.esa.core.helper.StringConcatenationChecker;
import com.bartlomiejpluta.esa.core.java.JavaSyntaxRegexProvider;
import com.bartlomiejpluta.esa.core.plugin.*;
import com.bartlomiejpluta.esa.core.xml.XmlHelper;
import com.bartlomiejpluta.esa.file.matcher.PackageNameMatcher;
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
    public Plugin loggingPlugin(StaticScopeHelper staticScopeHelper, StringConcatenationChecker stringConcatenationChecker) {
        return new LoggingPlugin(staticScopeHelper, stringConcatenationChecker);
    }

    @Provides
    @IntoSet
    public Plugin debuggablePlugin(XmlHelper xmlHelper) {
        return new DebuggablePlugin(xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin allowBackupPlugin(XmlHelper xmlHelper) {
        return new AllowBackupPlugin(xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin permissionRaceConditionPlugin(XmlHelper xmlHelper) {
        return new PermissionsRaceConditionPlugin(xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin secureRandomPlugin() {
        return new SecureRandomPlugin();
    }

    @Provides
    @IntoSet
    public Plugin implicitIntentsPlugin(JavaSyntaxRegexProvider javaSyntaxRegexProvider) {
        return new ImplicitIntentsPlugin(javaSyntaxRegexProvider);
    }

    @Provides
    @IntoSet
    public Plugin sharedUidPlugin(XmlHelper xmlHelper) {
        return new SharedUidPlugin(xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin usesSdkPlugin(XmlHelper xmlHelper) {
        return new UsesSdkPlugin(xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin cipherInstancePlugin(StaticScopeHelper staticScopeHelper) {
        return new CipherInstancePlugin(staticScopeHelper);
    }

    @Provides
    @IntoSet
    public Plugin strictModePlugin(StaticScopeHelper staticScopeHelper) {
        return new StrictModePlugin(staticScopeHelper);
    }

    @Provides
    @IntoSet
    public Plugin externalStoragePlugin(ParentNodeFinder parentNodeFinder, StaticScopeHelper staticScopeHelper) {
        return new ExternalStoragePlugin(parentNodeFinder, staticScopeHelper);
    }

    @Provides
    @IntoSet
    public Plugin suppressWarningsPlugin() {
        return new SuppressWarningsPlugin();
    }

    @Provides
    @IntoSet
    public Plugin exportedComponentsPlugin(XmlHelper xmlHelper, PackageNameMatcher packageNameMatcher) {
        return new ExportedComponentsPlugin(xmlHelper, packageNameMatcher);
    }

    @Provides
    @IntoSet
    public Plugin dangerousPermissionPlugin(XmlHelper xmlHelper) {
        return new DangerousPermissionPlugin(xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin textInputValidationPlugin(XmlHelper xmlHelper) {
        return new TextInputValidationPlugin(xmlHelper);
    }

    @Provides
    @IntoSet
    public Plugin intentFilterPlugin(XmlHelper xmlHelper, PackageNameMatcher packageNameMatcher) {
        return new IntentFilterPlugin(xmlHelper, packageNameMatcher);
    }

    @Provides
    @IntoSet
    public Plugin sqlInjectionPlugin(StringConcatenationChecker stringConcatenationChecker) {
        return new SqlInjectionPlugin( stringConcatenationChecker);
    }

    @Provides
    @IntoSet
    public Plugin worldAccessPermissionsPlugin() {
        return new WorldAccessPermissionsPlugin();
    }

    @Provides
    @IntoSet
    public Plugin orderedAndStickyBroadcastPlugin() {
        return new OrderedBroadcastPlugin();
    }

    @Provides
    @IntoSet
    public Plugin webViewPlugin() {
        return new WebViewPlugin();
    }

    @Provides
    @IntoSet
    public Plugin telephonyManagerPlugin() {
        return new TelephonyManagerPlugin();
    }
}
