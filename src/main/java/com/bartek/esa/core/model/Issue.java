package com.bartek.esa.core.model;

import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
public class Issue {
    private final Class<?> issuer;
    private final String descriptionCode;
    private final File file;
    private final Integer lineNumber;
    private final String line;
}
