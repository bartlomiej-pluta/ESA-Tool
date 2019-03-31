package com.bartek.esa.core.archetype;

import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathFactory;
import java.io.File;

public abstract class XmlPlugin extends BasePlugin {

    @Override
    protected void run(File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder = Try.of(factory::newDocumentBuilder).getOrElseThrow(EsaException::new);
        Document document = Try.of(() -> builder.parse(file)).getOrElseThrow(EsaException::new);
        run(document);
    }

    protected abstract void run(Document xml);

    protected Object xPath(Document xml, String expression, QName returnType) {
        return Try.of(() -> XPathFactory.newDefaultInstance().newXPath().evaluate(expression, xml, returnType))
                .getOrElseThrow(EsaException::new);
    }
}
