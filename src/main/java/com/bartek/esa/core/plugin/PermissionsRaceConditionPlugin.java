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

import static java.lang.Integer.parseInt;

public class PermissionsRaceConditionPlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public PermissionsRaceConditionPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        boolean isAnyPermissionDefined = ((NodeList) xmlHelper.xPath(manifest.getModel(), "/manifest/permission", XPathConstants.NODESET)).getLength() > 0;
        if(isAnyPermissionDefined) {
            Node usesSdkNode = (Node) xmlHelper.xPath(manifest.getModel(), "/manifest/uses-sdk", XPathConstants.NODE);
            Node minSdkVersionNode = usesSdkNode.getAttributes().getNamedItem("android:minSdkVersion");
            int minSdkVersion = parseInt(minSdkVersionNode.getNodeValue());
            if(minSdkVersion < 21) {
                addIssue(Severity.VULNERABILITY, getModel(minSdkVersion), manifest.getFile(), null, minSdkVersionNode.toString());
            }
        }
    }

    private Map<String, String> getModel(int minSdkVersion) {
        return Map.of("minSdkVersion", Integer.toString(minSdkVersion));
    }
}
