package com.api.server;

import com.api.controller.HealthCheckController;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServer {
    private static final Logger logger = LogManager.getLogger(SimpleHttpServer.class);
    private static HttpServer server;

    public static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/health", new HealthCheckController());
        server.setExecutor(null);
        server.start();
        logger.info("REST API started at http://localhost:8080/api/");
    }

    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            logger.info("REST API server stopped.");
        }
    }
}