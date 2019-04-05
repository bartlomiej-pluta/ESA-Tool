package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.util.Optional;

public class SharedUidPlugin extends AndroidManifestPlugin {

    @Inject
    public SharedUidPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        Node manifestNode = (Node) xPath(xml, "/manifest", XPathConstants.NODE);
        Optional.ofNullable(manifestNode.getAttributes().getNamedItem("android:sharedUserId")).ifPresent(node -> {
            addIssue(Severity.VULNERABILITY, null, node.toString());
        });
    }
}
