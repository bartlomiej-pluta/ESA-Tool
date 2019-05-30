package com.bartek.esa.core.plugin;

import com.bartek.esa.core.archetype.ResourceLayoutPlugin;
import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import java.util.Optional;

public class TextInputValidationPlugin extends ResourceLayoutPlugin {

    @Inject
    public TextInputValidationPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        super(globMatcher, xmlHelper);
    }

    @Override
    protected void run(Document xml) {
        NodeList editTextNodes = xml.getElementsByTagName("EditText");
        stream(editTextNodes)
                .filter(this::doesNotHaveInputType)
                .forEach(n -> addIssue(Severity.WARNING, null, tagString(n)));
    }

    private boolean doesNotHaveInputType(Node editText) {
        Boolean doesHaveInputType = Optional.ofNullable(editText.getAttributes().getNamedItem("android:inputType"))
                .map(Node::getNodeValue)
                .map(v -> !v.isEmpty())
                .orElse(false);
        return !doesHaveInputType;
    }
}
