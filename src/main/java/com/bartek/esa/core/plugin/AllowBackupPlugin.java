package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.util.Optional;

public class AllowBackupPlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public AllowBackupPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        Node applicationNode = (Node) xmlHelper.xPath(manifest.getModel(), "/manifest/application", XPathConstants.NODE);
        Optional.ofNullable(applicationNode.getAttributes().getNamedItem("android:allowBackup")).ifPresentOrElse(n -> {
            if (!n.getNodeValue().equals("false")) {
                addIssue(Severity.WARNING, ".NO_FALSE", manifest.getFile(), null, n.toString());
            }
        }, () -> addIssue(Severity.ERROR, ".NO_ATTR", manifest.getFile(), null, null));
    }
}
