package com.bartek.esa.core.archetype;

import com.bartek.esa.core.model.enumeration.Severity;
import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.core.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BasePlugin implements Plugin {
    private final XmlHelper xmlHelper;
    private Set<Issue> issues = new HashSet<>();
    private Document manifest;
    private File file;

    public BasePlugin(XmlHelper xmlHelper) {
        this.xmlHelper = xmlHelper;
    }

    @Override
    public void update(File file, Document manifest) {
        this.file = file;
        this.manifest = manifest;
        this.issues.clear();
    }

    @Override
    public Set<Issue> runForIssues() {
       if(canBeExecuted()) {
           run(file);
       }
        return issues;
    }

    private boolean canBeExecuted() {
        return hasDeclaredApiVersion();
    }

    private boolean hasDeclaredApiVersion() {
        Node usesSdkNode = (Node) xmlHelper.xPath(manifest, "/manifest/uses-sdk", XPathConstants.NODE);
        if(usesSdkNode == null) {
            Issue issue = Issue.builder()
                    .issuer(BasePlugin.class)
                    .descriptionCode(".NO_USES_SDK")
                    .severity(Severity.ERROR)
                    .build();
            addIssue(issue);

            return false;
        }

        if(usesSdkNode.getAttributes().getNamedItem("android:minSdkVersion") == null) {
            Issue issue = Issue.builder()
                    .issuer(BasePlugin.class)
                    .descriptionCode(".USES_SDK.NO_MIN_SDK_VERSION")
                    .severity(Severity.ERROR)
                    .build();
            addIssue(issue);

            return false;
        }

        return true;
    }

    protected abstract void run(File file);

    protected void addIssue(Severity severity, Integer lineNumber, String line) {
        addIssue(severity, "", lineNumber, line);
    }

    protected void addIssue(Severity severity, Map<String, String> descriptionModel, Integer lineNumber, String line) {
        addIssue(severity, "", descriptionModel, lineNumber, line);
    }


    protected void addIssue(Severity severity, String descriptionCode, Integer lineNumber, String line) {
        addIssue(severity, descriptionCode, new HashMap<>(), lineNumber, line);
    }

    protected void addIssue(Severity severity, String descriptionCode, Map<String, String> descriptionModel, Integer lineNumber, String line) {
        Issue issue = Issue.builder()
                .severity(severity)
                .issuer(this.getClass())
                .descriptionCode(descriptionCode)
                .descriptionModel(descriptionModel)
                .file(file)
                .lineNumber(lineNumber)
                .line(line)
                .build();

        issues.add(issue);
    }

    protected void addIssue(Issue issue) {
        issues.add(issue);
    }

    protected File getOriginalFile() {
        return file;
    }

    protected Document getManifest() {
        return manifest;
    }
}
