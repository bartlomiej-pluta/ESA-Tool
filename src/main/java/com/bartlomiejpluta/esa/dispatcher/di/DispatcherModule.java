package com.bartlomiejpluta.esa.dispatcher.di;

import com.bartlomiejpluta.esa.dispatcher.dispatcher.MethodDispatcher;
import dagger.Module;
import dagger.Provides;

@Module
public class DispatcherModule {

    @Provides
    MethodDispatcher methodDispatcher() {
        return new MethodDispatcher();
    }
}
