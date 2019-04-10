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
import java.util.Optional;

public class DangerousPermissionPlugin extends AndroidManifestPlugin {

    @Inject
    public DangerousPermissionPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        NodeList customPermissions = (NodeList) xPath(xml, "/manifest/permission", XPathConstants.NODESET);
        stream(customPermissions)
                .filter(this::isDangerousPermission)
                .filter(this::doesNotHaveDescription)
                .forEach(permission -> addIssue(Severity.WARNING, null, tagString(permission)));
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
