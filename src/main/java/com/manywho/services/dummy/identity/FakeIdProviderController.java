package com.manywho.services.dummy.identity;

import okhttp3.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Path("/callback")
public class FakeIdProviderController {
    @Path("/fake-idp")
    @GET
    public Response fakeIdProvider(
            @QueryParam("state") String state,
            @QueryParam("redirect_uri") String redirectUri,
            @QueryParam("oauth_token") String oauthToken,
            @QueryParam("RelayState") String relayState
    ) throws IOException {

        String decodedRedirectUri = URLDecoder.decode(redirectUri, StandardCharsets.UTF_8.toString());

        if (relayState == null || relayState.isEmpty()) {
            // this url is for oauth 1.0
            String url = String.format("%s?oauth_token=%s&state=%s&redirect_uri=%s", decodedRedirectUri, oauthToken, state, decodedRedirectUri);

            //this url is for oauth2
            if (oauthToken == null || oauthToken.isEmpty()) {
                url = String.format("%s?code=1234&state=%s&redirect_uri=%s", decodedRedirectUri, state, decodedRedirectUri);
            }

            return Response.seeOther(URI.create(url)).build();
        }

        // those lines are for SAML
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

        try (okhttp3.Response response = client.newCall(request).execute()) {
            return Response.seeOther(URI.create(response.request().url().toString())).build();
        }
    }
}
