package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.AndroidManifestPlugin;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.bartlomiejpluta.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.util.Optional;

public class DebuggablePlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public DebuggablePlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        Node applicationNode = (Node) xmlHelper.xPath(manifest.getModel(), "/manifest/application", XPathConstants.NODE);
        Optional.ofNullable(applicationNode.getAttributes().getNamedItem("android:debuggable")).ifPresentOrElse(n -> {
            if(!n.getNodeValue().equals("false")) {
                addIssue(Severity.WARNING, ".NO_FALSE", manifest.getFile(),null, n.toString());
            }
        }, () -> addIssue(Severity.ERROR, ".NO_ATTR", manifest.getFile(), null, null));
    }
}
