package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;

public class ExportedComponentsPlugin extends AndroidManifestPlugin {

    @Inject
    public ExportedComponentsPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        findExportedComponents(xml, "activity");
        findExportedComponents(xml, "service");
        findExportedComponents(xml, "receiver");
        findExportedProviders(xml);
    }

    private void findExportedComponents(Document xml, String component) {
        NodeList exportedActivities = (NodeList) xPath(xml, format("/manifest/application/%s", component), XPathConstants.NODESET);
        stream(exportedActivities)
                .filter(this::isExported)
                .filter(node -> doesNotHavePermission(node, "android:permission"))
                .forEach(node -> addIssue(Severity.WARNING, ".NO_PERMISSION", getModel(node), null, null));
    }

    private Map<String, String> getModel(Node node) {
        return Map.of(
                "componentName", node.getAttributes().getNamedItem("android:name").getNodeValue(),
                "componentType", node.getNodeName()
        );
    }

    private void findExportedProviders(Document xml) {
        NodeList exportedProviders = (NodeList) xPath(xml, "/manifest/application/provider", XPathConstants.NODESET);
        stream(exportedProviders)
                .filter(this::isExported)
                .filter(node -> doesNotHavePermission(node, "android:writePermission")
                        || doesNotHavePermission(node, "android:readPermission"))
                .forEach(node -> addIssue(Severity.WARNING, ".NO_PERMISSION", getModel(node), null, null));
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

    private String nodeToString(Node node) {
        String nodeName = Optional.ofNullable(node.getAttributes().getNamedItem("android:name"))
                .map(Node::getNodeValue)
                .map(name -> format(" android:name=\"%s\"", name))
                .orElse("");

        return format("<%s%s ...", node.getNodeName(), nodeName);
    }
}
