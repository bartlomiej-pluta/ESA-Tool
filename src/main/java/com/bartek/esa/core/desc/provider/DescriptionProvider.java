package com.bartek.esa.core.desc.provider;

import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;
import org.apache.commons.text.StringSubstitutor;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Properties;

public class DescriptionProvider {
    private static final String DESCRIPTION_FILE = "description.properties";
    private final Properties descriptions = new Properties();

    @Inject
    public DescriptionProvider() {
        Optional.ofNullable(DescriptionProvider.class.getClassLoader().getResourceAsStream(DESCRIPTION_FILE))
                .ifPresent(p -> Try.run(() -> descriptions.load(p)).getOrElseThrow(EsaException::new));
    }

    public String getDescriptionForIssue(Issue issue) {
        String code = issue.getIssuer().getCanonicalName() + issue.getDescriptionCode();
        String description = StringSubstitutor.replace(descriptions.getProperty(code), issue.getDescriptionModel());
        return description != null && !description.isEmpty() ? description : "No description provided.";
    }
}
