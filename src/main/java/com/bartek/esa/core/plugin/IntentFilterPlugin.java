package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.AndroidManifestPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import java.util.Map;

public class IntentFilterPlugin extends AndroidManifestPlugin {

    @Inject
    public IntentFilterPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        NodeList filters = xml.getElementsByTagName("intent-filter");
        stream(filters)
                .filter(this::isNotMainActivity)
                .map(Node::getParentNode)
                .forEach(n -> addIssue(Severity.INFO, getModel(n), null, null));
    }

    private Map<String, String> getModel(Node node) {
        return Map.of(
                "componentType", node.getNodeName(),
                "componentName", node.getAttributes().getNamedItem("android:name").getNodeValue()
        );
    }

    private boolean isNotMainActivity(Node filter) {
        long mainActivityIntentFilters = stream(filter.getChildNodes())
                .filter(n -> n.getNodeName().matches("action|category"))
                .map(n -> n.getAttributes().getNamedItem("android:name"))
                .map(Node::getNodeValue)
                .filter(v -> v.equals("android.intent.action.MAIN") || v.equals("android.intent.category.LAUNCHER"))
                .count();

        long currentIntentFilters = stream(filter.getChildNodes())
                .filter(n -> n.getNodeName().matches("action|category"))
                .count();

        return mainActivityIntentFilters != currentIntentFilters;
    }
}
