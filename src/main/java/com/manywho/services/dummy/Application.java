package com.manywho.services.dummy;

import com.manywho.sdk.services.servers.EmbeddedServer;
import com.manywho.sdk.services.servers.Servlet3Server;
import com.manywho.sdk.services.servers.undertow.UndertowServer;

import javax.ws.rs.ApplicationPath;

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
        
        Boolean useV2 = args.length > 0 && args[0].equalsIgnoreCase("true");

        if(useV2){
            server.start(
                "api/dummy/2", 
                8081, 
                ClassLoader.getSystemClassLoader().getResourceAsStream("server_keystore.jks"), 
                ClassLoader.getSystemClassLoader().getResourceAsStream("server_truststore.jks"), 
                "secret", 
                "secret");
        }
        else{
            server.start("api/dummy/1");
        }
    }
}
