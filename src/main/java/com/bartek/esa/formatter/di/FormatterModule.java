package com.bartek.esa.formatter.di;

import com.bartek.esa.core.desc.provider.DescriptionProvider;
import com.bartek.esa.formatter.formatter.ColorFormatter;
import com.bartek.esa.formatter.formatter.SimpleFormatter;
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
}
