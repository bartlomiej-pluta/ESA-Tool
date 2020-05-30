package com.bartlomiejpluta.esa.formatter.formatter;

import com.bartlomiejpluta.esa.core.model.object.Issue;
import com.bartlomiejpluta.esa.error.EsaException;
import com.bartlomiejpluta.esa.formatter.archetype.Formatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;

import java.util.Set;

public class JsonFormatter implements Formatter {

    @Override
    public void beforeFormat() {
        // nothing to do
    }

    @Override
    public String format(Set<Issue> issues) {
        ObjectMapper objectMapper = new ObjectMapper();
        return Try.of(() -> objectMapper.writeValueAsString(issues))
                .getOrElseThrow(EsaException::new);
    }

    @Override
    public void afterFormat() {
        // nothing to do
    }
}
