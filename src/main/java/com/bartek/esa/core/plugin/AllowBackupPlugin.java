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

public class AllowBackupPlugin extends AndroidManifestPlugin {

    @Inject
    public AllowBackupPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        Node applicationNode = (Node) xPath(xml, "/manifest/application", XPathConstants.NODE);
        Optional.ofNullable(applicationNode.getAttributes().getNamedItem("android:allowBackup")).ifPresentOrElse(n -> {
            if (!n.getNodeValue().equals("false")) {
                addIssue(Severity.WARNING, ".NO_FALSE", null, n.toString());
            }
        }, () -> addIssue(Severity.WARNING, ".NO_ATTR", null, null));
    }
}
