package com.bartek.esa.formatter.formatter;

import com.bartek.esa.core.model.object.Issue;
import com.bartek.esa.error.EsaException;
import com.bartek.esa.formatter.archetype.Formatter;
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
