package com.manywho.services.dummy;

import com.manywho.sdk.services.BaseApplication;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationPath("/")
public class Application extends BaseApplication {
    public Application() {
        registerSdk().packages("com.manywho.services.dummy");
    }

    public static void main(String[] args) {
        try {
            final String port = System.getProperty("server.port") != null ? System.getProperty("server.port") : "8080";

            final URI BASE_URI = URI.create(String.format("http://0.0.0.0:%s/api/dummy/1", port));

            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new Application(), false);

            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

            server.start();

            System.out.println(String.format("Application started.\nTry out %s\nStop the application using CTRL+C", BASE_URI));

            Thread.currentThread().join();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}