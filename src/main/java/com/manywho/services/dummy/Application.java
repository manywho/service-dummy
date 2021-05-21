package com.manywho.services.dummy;

import java.io.FileInputStream;

import javax.ws.rs.ApplicationPath;

import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.Servlet3Server;
import com.manywho.sdk.services.servers.undertow.UndertowServer;

@ApplicationPath("api/dummy/1")
public class Application extends Servlet3Server {
    public Application(){
        this.addModule(new ApplicationModule());
        this.setApplication(Application.class);
        this.start();
    }

    public static void main(String[] args) throws Exception {
        EmbeddedServer server = new UndertowServer();
        server.addModule(new ApplicationModule());
        server.setApplication(Application.class);

        String V2 = System.getenv("DUMMY_V2");
        String keystore = System.getenv("DUMMY_KEYSTORE");
        String truststore = System.getenv("DUMMY_TRUSTSTORE");
        String keystorePassword = System.getenv("DUMMY_KEYSTORE_PASSWORD");
        String truststorePassword = System.getenv("DUMMY_TRUSTSTORE_PASSWORD");

        if(V2 != null && V2.equalsIgnoreCase("true")) {
            server.start(
                "",
                8081,
                keystore != null && !keystore.isEmpty() ? new FileInputStream(keystore) : null,
                truststore != null && !truststore.isEmpty() ? new FileInputStream(truststore) : null,
                keystorePassword,
                truststorePassword);
        }
        else{
            server.start();
        }
    }
}
