package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.inject.Inject;
import javax.xml.xpath.XPathConstants;
import java.util.Map;
import java.util.Optional;

public class UsesSdkPlugin extends AndroidManifestPlugin {

    @Inject
    public UsesSdkPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        Optional.ofNullable((Node) xPath(xml, "/manifest/uses-sdk", XPathConstants.NODE)).ifPresentOrElse(usesSdkNode -> {
            if(usesSdkNode.getAttributes().getNamedItem("android:minSdkVersion") == null) {
                addIssue(Severity.ERROR, ".USES_SDK.NO_MIN_SDK_VERSION", null, null);
            }

            Optional.ofNullable(usesSdkNode.getAttributes().getNamedItem("android:maxSdkVersion")).ifPresent(maxSdkVersion ->
                addIssue(Severity.ERROR, ".USES_SDK.MAX_SDK_VERSION", Map.of("maxSdkVersion", maxSdkVersion.getNodeValue()),null, maxSdkVersion.toString())
            );
        }, () -> addIssue(Severity.ERROR, ".NO_USES_SDK", null, null));
    }
}
