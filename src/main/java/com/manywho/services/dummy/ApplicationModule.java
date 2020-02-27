package com.manywho.services.dummy;

import com.google.inject.AbstractModule;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.client.run.RunClientProvider;
import com.manywho.services.dummy.database.DummyTypeProvider;
import com.manywho.sdk.services.types.TypeProvider;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(DummyTypeProvider.class);
        bind(RunClient.class).toProvider(RunClientProvider.class);
    }
}
