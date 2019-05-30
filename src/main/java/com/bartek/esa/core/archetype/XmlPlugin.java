package com.bartek.esa.core.archetype;

import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public abstract class XmlPlugin extends BasePlugin {
    private final GlobMatcher globMatcher;
    private final XmlHelper xmlHelper;

    public XmlPlugin(GlobMatcher globMatcher, XmlHelper xmlHelper) {
        this.globMatcher = globMatcher;
        this.xmlHelper = xmlHelper;
    }

    @Override
    public boolean supports(File file) {
        return globMatcher.fileMatchesGlobPattern(file, "**/*.xml");
    }

    @Override
    protected void run(File file) {
        Document xml = xmlHelper.parseXml(file);
        run(xml);
    }

    protected abstract void run(Document xml);

    protected Object xPath(Document xml, String expression, QName returnType) {
        return xmlHelper.xPath(xml, expression, returnType);
    }

    protected Stream<Node> stream(NodeList nodeList) {
        return xmlHelper.stream(nodeList);
    }

    protected String tagString(Node node) {
        Node[] attributes = new Node[node.getAttributes().getLength()];
        for(int i=0; i<attributes.length; ++i) {
            attributes[i] = node.getAttributes().item(i);
        }

        String attributesString = Arrays.stream(attributes)
                .map(n -> format("%s=\"%s\"", n.getNodeName(), n.getNodeValue()))
                .collect(Collectors.joining(" "));

        return format("<%s %s ...", node.getNodeName(), attributesString);
    }
}
