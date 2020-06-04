package com.manywho.services.dummy;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.configuration.Configuration;

public class ApplicationConfiguration implements Configuration {

    @Configuration.Setting(name = "Include Types With Bindings", contentType = ContentType.Boolean, required = false)
    private boolean includeTypesWithBindings;

    public boolean getIncludeTypesWithBindings() {
        return includeTypesWithBindings;
    }

    @Configuration.Setting(name = "Hostname", contentType = ContentType.String, required = false)
    private String hostname;

    @Configuration.Setting(name = "Port", contentType = ContentType.Number, required = false)
    private int port;

    @Configuration.Setting(name = "Authorization Type", contentType = ContentType.String, required = false)
    private String authorizationType;

    public String getAuthorizationType() {
        return authorizationType;
    }
}
