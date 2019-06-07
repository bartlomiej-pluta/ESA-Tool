package com.bartek.esa.formatter.di;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.formatter.formatter.ColorFormatter;
import com.bartek.esa.formatter.formatter.JsonFormatter;
import com.bartek.esa.formatter.formatter.SimpleFormatter;
import com.bartek.esa.formatter.provider.FormatterProvider;
import dagger.Module;
import dagger.Provides;

@Module
public class FormatterModule {

    @Provides
    public SimpleFormatter simpleFormatter(DescriptionProvider descriptionProvider) {
        return new SimpleFormatter(descriptionProvider);
    }

    @Provides
    public ColorFormatter colorFormatter(DescriptionProvider descriptionProvider) {
        return new ColorFormatter(descriptionProvider);
    }

    @Provides
    public JsonFormatter jsonFormatter() {
        return new JsonFormatter();
    }

    @Provides
    public FormatterProvider formatterProvider(SimpleFormatter simpleFormatter, ColorFormatter colorFormatter, JsonFormatter jsonFormatter) {
        return new FormatterProvider(simpleFormatter, colorFormatter, jsonFormatter);
    }
}
