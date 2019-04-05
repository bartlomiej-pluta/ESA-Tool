package com.bartek.esa.core.archetype;

import com.bartek.esa.core.xml.XmlHelper;
import com.bartek.esa.file.matcher.GlobMatcher;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import java.io.File;

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
}
