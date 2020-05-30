package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.util.Map;
import java.util.Optional;

public class UsesSdkPlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public UsesSdkPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        Optional.ofNullable((Node) xmlHelper.xPath(manifest.getModel(), "/manifest/uses-sdk", XPathConstants.NODE)).ifPresentOrElse(usesSdkNode -> {
            if(usesSdkNode.getAttributes().getNamedItem("android:minSdkVersion") == null) {
                addIssue(Severity.ERROR, ".USES_SDK.NO_MIN_SDK_VERSION", manifest.getFile(), null, null);
            }

            Optional.ofNullable(usesSdkNode.getAttributes().getNamedItem("android:maxSdkVersion")).ifPresent(maxSdkVersion ->
                addIssue(Severity.ERROR, ".USES_SDK.MAX_SDK_VERSION", Map.of("maxSdkVersion", maxSdkVersion.getNodeValue()), manifest.getFile(), null, maxSdkVersion.toString())
            );
        }, () -> addIssue(Severity.ERROR, ".NO_USES_SDK", manifest.getFile(), null, null));
    }
}
