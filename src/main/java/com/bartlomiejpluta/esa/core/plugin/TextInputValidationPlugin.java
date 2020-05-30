package com.bartlomiejpluta.esa.core.plugin;

import com.bartlomiejpluta.esa.context.model.Source;
import com.bartlomiejpluta.esa.core.archetype.ResourceLayoutPlugin;
import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import com.bartlomiejpluta.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import java.util.Optional;

public class TextInputValidationPlugin extends ResourceLayoutPlugin {
    private final XmlHelper xmlHelper;

    @Inject
    public TextInputValidationPlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    protected void run(Source<Document> layout) {
        NodeList editTextNodes = layout.getModel().getElementsByTagName("EditText");
        xmlHelper.stream(editTextNodes)
                .filter(this::doesNotHaveInputType)
                .forEach(n -> addXmlIssue(Severity.WARNING, layout.getFile(), n));
    }

    private boolean doesNotHaveInputType(Node editText) {
        Boolean doesHaveInputType = Optional.ofNullable(editText.getAttributes().getNamedItem("android:inputType"))
                .map(Node::getNodeValue)
                .map(v -> !v.isEmpty())
                .orElse(false);
        return !doesHaveInputType;
    }
}
