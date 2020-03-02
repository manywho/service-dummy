package com.manywho.services.dummy;

import java.io.FileInputStream;

import javax.ws.rs.ApplicationPath;

import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.Servlet3Server;
import com.manywho.sdk.services.servers.undertow.UndertowServer;

@ApplicationPath("/")
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

        if(V2.equalsIgnoreCase("true")){          
            server.start(
                "api/dummy/2", 
                8081, 
                new FileInputStream(keystore), 
                truststore != null && !truststore.isEmpty() ? new FileInputStream(truststore) : null, 
                "secret", 
                "secret");
        }
        else{
            server.start("api/dummy/1");
        }
    }
}
