package com.bartek.esa.core.desc.provider;

import com.bartek.esa.error.EsaException;
import io.vavr.control.Try;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Properties;

public class DescriptionProvider {
    private static final String DESCRIPTION_FILE = "description.properties";
    private final Properties properties = new Properties();

    @Inject
    public DescriptionProvider() {
        Optional.ofNullable(DescriptionProvider.class.getClassLoader().getResourceAsStream(DESCRIPTION_FILE))
                .ifPresent(p -> Try.run(() -> properties.load(p)).getOrElseThrow(EsaException::new));
    }

    public String getDescriptionForClass(Class<?> clazz) {
        String clazzCanonicalName = properties.getProperty(clazz.getCanonicalName());
        return clazzCanonicalName != null ? clazzCanonicalName : "No description provided.";
    }
}
