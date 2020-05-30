package com.bartlomiejpluta.esa.core.model.object;

import com.bartlomiejpluta.esa.core.model.enumeration.Severity;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Data
@Builder
public class Issue implements Comparable {
    private final Class<?> issuer;
    private final Severity severity;
    private final String descriptionCode;
    private final Map<String, String> descriptionModel;
    private final File file;
    private final Integer lineNumber;
    private final String line;

    @Override
    public int compareTo(Object o) {
        Issue another = (Issue) o;
        int compByFile = file.compareTo(another.file);
        if(compByFile != 0) {
            return compByFile;
        }

        return Optional.ofNullable(lineNumber).orElse(0) - Optional.ofNullable(another.lineNumber).orElse(0);
    }
}
