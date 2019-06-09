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
import java.util.Optional;

public class DangerousPermissionPlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public DangerousPermissionPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        NodeList customPermissions = (NodeList) xmlHelper.xPath(manifest.getModel(), "/manifest/permission", XPathConstants.NODESET);
        xmlHelper.stream(customPermissions)
                .filter(this::isDangerousPermission)
                .filter(this::doesNotHaveDescription)
                .forEach(permission -> addXmlIssue(Severity.WARNING, manifest.getFile(), permission));
    }

    private boolean isDangerousPermission(Node permission) {
        return Optional.ofNullable(permission.getAttributes().getNamedItem("android:protectionLevel"))
                .map(Node::getNodeValue)
                .map(v -> v.equals("dangerous"))
                .orElse(false);
    }

    private boolean doesNotHaveDescription(Node permission) {
        Boolean doesHaveDescription = Optional.ofNullable(permission.getAttributes().getNamedItem("android:description"))
                .map(Node::getNodeValue)
                .map(v -> !v.isEmpty())
                .orElse(false);

        return !doesHaveDescription;
    }

}
