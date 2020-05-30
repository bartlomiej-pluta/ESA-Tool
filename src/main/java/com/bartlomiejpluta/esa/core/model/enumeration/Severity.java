package com.bartlomiejpluta.esa.core.model.enumeration;

import lombok.Getter;

@Getter
public enum Severity {
    INFO(false),
    WARNING(false),
    ERROR(true),
    VULNERABILITY(true);

    private final boolean exitWithError;

    Severity(boolean exitWithError) {
        this.exitWithError = exitWithError;
    }
}
