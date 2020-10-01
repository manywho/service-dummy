package com.manywho.services.dummy;

import com.google.inject.AbstractModule;
import com.manywho.sdk.client.run.RunClient;
import com.manywho.sdk.client.run.RunClientProvider;
import com.manywho.sdk.services.types.TypeProvider;
import com.manywho.services.dummy.database.DummyTypeProvider;
import com.manywho.services.dummy.providers.Sql2oProvider;
import org.sql2o.Sql2o;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TypeProvider.class).to(DummyTypeProvider.class);
        bind(RunClient.class).toProvider(RunClientProvider.class);
        bind(Sql2o.class).toProvider(Sql2oProvider.class);
    }
}
