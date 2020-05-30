package com.bartlomiejpluta.esa.core.xml;

import com.bartlomiejpluta.esa.error.EsaException;
import io.vavr.control.Try;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public class XmlHelper {

    @Inject
    public XmlHelper() {

    }

    public Document parseXml(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder = Try.of(factory::newDocumentBuilder).getOrElseThrow(EsaException::new);
        return Try.of(() -> builder.parse(file)).getOrElseThrow(EsaException::new);
    }

    public Object xPath(Document xml, String expression, QName returnType) {
        return Try.of(() -> XPathFactory.newDefaultInstance().newXPath().evaluate(expression, xml, returnType))
                .getOrElseThrow(EsaException::new);
    }

    public Stream<Node> stream(NodeList nodeList) {
        Node[] nodes = new Node[nodeList.getLength()];
        for (int i=0; i<nodeList.getLength(); ++i) {
            nodes[i] = nodeList.item(i);
        }

        return Arrays.stream(nodes);
    }
}
