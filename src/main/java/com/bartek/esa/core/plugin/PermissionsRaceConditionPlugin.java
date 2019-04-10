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

import static java.lang.Integer.parseInt;

public class PermissionsRaceConditionPlugin extends AndroidManifestPlugin {

    @Inject
    public PermissionsRaceConditionPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        boolean isAnyPermissionDefined = ((NodeList) xPath(xml, "/manifest/permission", XPathConstants.NODESET)).getLength() > 0;
        if(isAnyPermissionDefined) {
            Node usesSdkNode = (Node) xPath(xml, "/manifest/uses-sdk", XPathConstants.NODE);
            Node minSdkVersionNode = usesSdkNode.getAttributes().getNamedItem("android:minSdkVersion");
            int minSdkVersion = parseInt(minSdkVersionNode.getNodeValue());
            if(minSdkVersion < 21) {
                addIssue(Severity.VULNERABILITY, getModel(minSdkVersion), null, minSdkVersionNode.toString());
            }
        }
    }

    private Map<String, String> getModel(int minSdkVersion) {
        return Map.of("minSdkVersion", Integer.toString(minSdkVersion));
    }
}
