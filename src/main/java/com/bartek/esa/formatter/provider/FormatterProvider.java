package com.bartek.esa.formatter.provider;

import com.bartek.esa.cli.model.object.CliArgsOptions;
import com.bartek.esa.formatter.archetype.Formatter;
import com.bartek.esa.formatter.formatter.ColorFormatter;
import com.bartek.esa.formatter.formatter.JsonFormatter;
import com.bartek.esa.formatter.formatter.SimpleFormatter;

import javax.inject.Inject;

public class FormatterProvider {
    private final SimpleFormatter simpleFormatter;
    private final ColorFormatter colorFormatter;
    private final JsonFormatter jsonFormatter;

    @Inject
    public FormatterProvider(SimpleFormatter simpleFormatter, ColorFormatter colorFormatter, JsonFormatter jsonFormatter) {
        this.simpleFormatter = simpleFormatter;
        this.colorFormatter = colorFormatter;
        this.jsonFormatter = jsonFormatter;
    }

    public Formatter provide(CliArgsOptions options) {
        switch (options.getOutputType()) {
            case COLOR:
                return colorFormatter;
            case JSON:
                return jsonFormatter;
            case DEFAULT:
                default:
                return simpleFormatter;
        }
    }
}
