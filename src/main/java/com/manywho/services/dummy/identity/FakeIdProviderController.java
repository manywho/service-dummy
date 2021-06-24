package com.manywho.services.dummy.identity;

import okhttp3.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import jdk.internal.org.jline.utils.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Path("/callback")
public class FakeIdProviderController {
    @Path("/fake-idp")
    @GET
    public Response fakeOauth2IdProvider(
            @QueryParam("state") String state,
            @QueryParam("redirect_uri") String redirectUri
    ) throws IOException {
        String decodedRedirectUri = URLDecoder.decode(redirectUri, StandardCharsets.UTF_8.toString());
        String url = String.format("%s?code=1234&state=%s&redirect_uri=%s", decodedRedirectUri, state, decodedRedirectUri);

        return Response.seeOther(URI.create(url)).build();
    }


    @Path("/fake-oauth1-idp")
    @GET
    public Response fakeOauth1IdProvider(
            @QueryParam("state") String state,
            @QueryParam("redirect_uri") String redirectUri,
            @QueryParam("oauth_token") String oauthToken
    ) throws IOException {
        String decodedRedirectUri = URLDecoder.decode(redirectUri, StandardCharsets.UTF_8.toString());
        String url = String.format("%s?oauth_token=%s&state=%s&redirect_uri=%s",
                decodedRedirectUri, oauthToken, state, decodedRedirectUri);

        return Response.seeOther(URI.create(url)).build();
    }

    @Path("/fake-saml-idp")
    @GET
    public Response fakeSamlIdProvider(
            @QueryParam("redirect_uri") String redirectUri,
            @QueryParam("RelayState") String relayState
    ) throws IOException {

        String decodedRedirectUri = URLDecoder.decode(redirectUri, StandardCharsets.UTF_8.toString());
        
        Log.info("Response received");

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MediaType.get("multipart/form-data"))
                .addFormDataPart("SAMLResponse", "123456")
                .addFormDataPart("RelayState", relayState)
                .build();

        String urlSam = String.format("%s", decodedRedirectUri);
        Request request = new Request.Builder()
                .url(urlSam)
                .post(requestBody)
                .build();

        Log.info("About to send request back to " + decodedRedirectUri);
        Log.info("URL SAML is " + urlSam);
        Log.info("Request body is " + requestBody);

        try (okhttp3.Response response = client.newCall(request).execute()) {
                Log.info("Flow response is " + response);
                Log.info("response.request().url() is " + response.request().url().toString());
                return Response.seeOther(URI.create(response.request().url().toString())).build();
        }
    }
}
