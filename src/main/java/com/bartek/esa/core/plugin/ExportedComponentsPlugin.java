package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class ExportedComponentsPlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public ExportedComponentsPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        findExportedComponents(manifest, "activity");
        findExportedComponents(manifest, "service");
        findExportedComponents(manifest, "receiver");
        findExportedProviders(manifest);
    }

    private void findExportedComponents(Source<Document> manifest, String component) {
        NodeList exportedActivities = (NodeList) xmlHelper.xPath(manifest.getModel(), format("/manifest/application/%s", component), XPathConstants.NODESET);
        xmlHelper.stream(exportedActivities)
                .filter(this::isExported)
                .filter(node -> doesNotHavePermission(node, "android:permission"))
                .forEach(node -> addIssue(Severity.WARNING, ".NO_PERMISSION", getModel(node), manifest.getFile(), null, null));
    }

    private Map<String, String> getModel(Node node) {
        return Map.of(
                "componentName", node.getAttributes().getNamedItem("android:name").getNodeValue(),
                "componentType", node.getNodeName()
        );
    }

    private void findExportedProviders(Source<Document> manifest) {
        NodeList exportedProviders = (NodeList) xmlHelper.xPath(manifest.getModel(), "/manifest/application/provider", XPathConstants.NODESET);
        xmlHelper.stream(exportedProviders)
                .filter(this::isExported)
                .filter(node -> doesNotHavePermission(node, "android:writePermission")
                        || doesNotHavePermission(node, "android:readPermission"))
                .forEach(node -> addIssue(Severity.WARNING, ".NO_PERMISSION", getModel(node), manifest.getFile(), null, null));
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

    // todo remove it!
    private String nodeToString(Node node) {
        String nodeName = Optional.ofNullable(node.getAttributes().getNamedItem("android:name"))
                .map(Node::getNodeValue)
                .map(name -> format(" android:name=\"%s\"", name))
                .orElse("");

        return format("<%s%s ...", node.getNodeName(), nodeName);
    }
}
