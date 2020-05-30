package com.bartlomiejpluta.esa.formatter.di;

import com.bartlomiejpluta.esa.core.desc.provider.DescriptionProvider;
import com.bartlomiejpluta.esa.formatter.formatter.ColorFormatter;
import com.bartlomiejpluta.esa.formatter.formatter.JsonFormatter;
import com.bartlomiejpluta.esa.formatter.formatter.SimpleFormatter;
import com.bartlomiejpluta.esa.formatter.provider.FormatterProvider;
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
