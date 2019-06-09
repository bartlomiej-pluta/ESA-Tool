package com.bartek.esa.core.plugin;

import com.bartek.esa.context.model.Source;
import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import java.util.Map;

public class IntentFilterPlugin extends AndroidManifestPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public IntentFilterPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> manifest) {
        NodeList filters = manifest.getModel().getElementsByTagName("intent-filter");
        xmlHelper.stream(filters)
                .filter(this::isNotMainActivity)
                .map(Node::getParentNode)
                .forEach(n -> addIssue(Severity.INFO, getModel(n), manifest.getFile(), null, null));
    }

    private Map<String, String> getModel(Node node) {
        return Map.of(
                "componentType", node.getNodeName(),
                "componentName", node.getAttributes().getNamedItem("android:name").getNodeValue()
        );
    }

    private boolean isNotMainActivity(Node filter) {
        long mainActivityIntentFilters = xmlHelper.stream(filter.getChildNodes())
                .filter(n -> n.getNodeName().matches("action|category"))
                .map(n -> n.getAttributes().getNamedItem("android:name"))
                .map(Node::getNodeValue)
                .filter(v -> v.equals("android.intent.action.MAIN") || v.equals("android.intent.category.LAUNCHER"))
                .count();

        long currentIntentFilters = xmlHelper.stream(filter.getChildNodes())
                .filter(n -> n.getNodeName().matches("action|category"))
                .count();

        return mainActivityIntentFilters != currentIntentFilters;
    }
}
