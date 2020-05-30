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
import javax.xml.xpath.XPathConstants;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class ExportedComponentsPlugin extends BasePlugin {
    private final XmlHelper xmlHelper;
    private final PackageNameMatcher packageNameMatcher;

    @Inject
    public ExportedComponentsPlugin(XmlHelper xmlHelper, PackageNameMatcher packageNameMatcher) {
        this.xmlHelper = xmlHelper;
        this.packageNameMatcher = packageNameMatcher;
    }

    @Override
    protected void run(Context context) {
        findExportedComponents(context, "activity");
        findExportedComponents(context, "service");
        findExportedComponents(context, "receiver");
        findExportedProviders(context);
    }

    private void findExportedComponents(Context context, String component) {
        NodeList exportedActivities = (NodeList) xmlHelper.xPath(context.getManifest().getModel(), format("/manifest/application/%s", component), XPathConstants.NODESET);
        xmlHelper.stream(exportedActivities)
                .filter(this::isExported)
                .filter(node -> doesNotHavePermission(node, "android:permission"))
                .forEach(node -> {
                    String componentName = node.getAttributes().getNamedItem("android:name").getNodeValue();
                    String canonicalName = context.getPackageName() + componentName;
                    if (isIntentDataBeingUsedInsideComponent(context, canonicalName)) {
                        addIssue(Severity.WARNING, ".NO_PERMISSION.DATA_USAGE", getModel(node), context.getManifest().getFile(), null, null);
                    } else {
                        addIssue(Severity.WARNING, ".NO_PERMISSION", getModel(node), context.getManifest().getFile(), null, null);
                    }
                });
    }

    private boolean isIntentDataBeingUsedInsideComponent(Context context, String componentCanonicalName) {
        return context.getJavaSources().stream()
                .filter(java -> packageNameMatcher.doesFileMatchPackageName(java.getFile(), componentCanonicalName))
                .flatMap(java -> java.getModel().findAll(MethodCallExpr.class).stream())
                .filter(expr -> expr.getName().getIdentifier().equals("getIntent"))
                .anyMatch(expr -> expr.getArguments().isEmpty());
    }

    private Map<String, String> getModel(Node node) {
        return Map.of(
                "componentName", node.getAttributes().getNamedItem("android:name").getNodeValue(),
                "componentType", node.getNodeName()
        );
    }

    private void findExportedProviders(Context context) {
        NodeList exportedProviders = (NodeList) xmlHelper.xPath(context.getManifest().getModel(), "/manifest/application/provider", XPathConstants.NODESET);
        xmlHelper.stream(exportedProviders)
                .filter(this::isExported)
                .filter(node -> doesNotHavePermission(node, "android:writePermission")
                        || doesNotHavePermission(node, "android:readPermission"))
                .forEach(node -> addIssue(Severity.WARNING, ".NO_PERMISSION", getModel(node), context.getManifest().getFile(), null, null));
    }

    private boolean doesNotHavePermission(Node node, String permissionAttribute) {
        Boolean doesHavePermission = Optional.ofNullable(node.getAttributes().getNamedItem(permissionAttribute))
                .map(Node::getNodeValue)
                .map(s -> !s.isEmpty())
                .orElse(false);
        return !doesHavePermission;
    }

    private boolean isExported(Node node) {
        return Optional.ofNullable(node.getAttributes().getNamedItem("android:exported"))
                .map(Node::getNodeValue)
                .map(v -> v.equals("true"))
                .orElse(false);
    }
}
