package com.bartek.esa.core.xml;

import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;
import org.w3c.dom.Document;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.File;

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
}
