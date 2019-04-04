package com.bartek.esa.formatter.provider;

import com.bartek.esa.cli.model.CliArgsOptions;
import com.bartek.esa.formatter.archetype.Formatter;
import com.bartek.esa.formatter.formatter.ColorFormatter;
import com.bartek.esa.formatter.formatter.SimpleFormatter;

import javax.inject.Inject;

public class FormatterProvider {
    private final SimpleFormatter simpleFormatter;
    private final ColorFormatter colorFormatter;

    @Inject
    public FormatterProvider(SimpleFormatter simpleFormatter, ColorFormatter colorFormatter) {
        this.simpleFormatter = simpleFormatter;
        this.colorFormatter = colorFormatter;
    }

    public Formatter provide(CliArgsOptions options) {
        return options.isColor() ? colorFormatter : simpleFormatter;
    }
}
