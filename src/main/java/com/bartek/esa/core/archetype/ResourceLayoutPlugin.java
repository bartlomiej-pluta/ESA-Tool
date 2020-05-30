package com.bartek.esa.core.archetype;

import com.bartek.esa.context.model.Context;
import com.bartek.esa.context.model.Source;
import org.w3c.dom.Document;

public abstract class ResourceLayoutPlugin extends BasePlugin {

    @Override
    protected void run(Context context) {
        context.getLayouts().forEach(this::run);
    }

    protected abstract void run(Source<Document> layout);
}
