package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Context;
import com.bartlomiejpluta.esa.core.archetype.BasePlugin;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.bartlomiejpluta.esa.core.xml.XmlHelper;
import com.bartlomiejpluta.esa.file.matcher.PackageNameMatcher;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;

public class IntentFilterPlugin extends BasePlugin {
    private final XmlHelper xmlHelper;
    private final PackageNameMatcher packageNameMatcher;

    @Inject
    public IntentFilterPlugin(XmlHelper xmlHelper, PackageNameMatcher packageNameMatcher) {
        this.xmlHelper = xmlHelper;
        this.packageNameMatcher = packageNameMatcher;
    }

    @Override
    protected void run(Context context) {
        NodeList filters = context.getManifest().getModel().getElementsByTagName("intent-filter");
        xmlHelper.stream(filters)
                .filter(this::isNotMainActivity)
                .filter(this::isNotExported)
                .map(Node::getParentNode)
                .forEach(node -> {
                    String componentName = node.getAttributes().getNamedItem("android:name").getNodeValue();
                    String canonicalName = context.getPackageName() + componentName;
                    if (isIntentDataBeingUsedInsideComponent(context, canonicalName)) {
                        addIssue(Severity.WARNING, ".DATA_USAGE", getModel(node), context.getManifest().getFile(), null, null);
                    } else {
                        addIssue(Severity.WARNING, getModel(node), context.getManifest().getFile(), null, null);
                    }
                });
    }

    private boolean isNotExported(Node component) {
        return !Optional.ofNullable(component.getAttributes().getNamedItem("android:exported"))
                .map(Node::getNodeValue)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    private Map<String, String> getModel(Node node) {
        return Map.of(
                "componentType", node.getNodeName(),
                "componentName", node.getAttributes().getNamedItem("android:name").getNodeValue()
        );
    }

    private boolean isNotMainActivity(Node filter) {
        long mainActivityIntentFilters = xmlHelper.stream(filter.getChildNodes())
                .filter(n -> n.getNodeName().matches("action|category|data"))
                .map(n -> n.getAttributes().getNamedItem("android:name"))
                .map(Node::getNodeValue)
                .filter(v -> v.equals("android.intent.action.MAIN") || v.equals("android.intent.category.LAUNCHER"))
                .count();

        long currentIntentFilters = xmlHelper.stream(filter.getChildNodes())
                .filter(n -> n.getNodeName().matches("action|category|data"))
                .count();

        return mainActivityIntentFilters != currentIntentFilters;
    }

    private boolean isIntentDataBeingUsedInsideComponent(Context context, String componentCanonicalName) {
        return context.getJavaSources().stream()
                .filter(java -> packageNameMatcher.doesFileMatchPackageName(java.getFile(), componentCanonicalName))
                .flatMap(java -> java.getModel().findAll(MethodCallExpr.class).stream())
                .filter(expr -> expr.getName().getIdentifier().equals("getIntent"))
                .anyMatch(expr -> expr.getArguments().isEmpty());
    }
}
