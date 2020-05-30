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

public class SharedUidPlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public SharedUidPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        Node manifestNode = (Node) xmlHelper.xPath(manifest.getModel(), "/manifest", XPathConstants.NODE);
        Optional.ofNullable(manifestNode.getAttributes().getNamedItem("android:sharedUserId")).ifPresent(node -> {
            addIssue(Severity.VULNERABILITY, manifest.getFile(), null, node.toString());
        });
    }
}
