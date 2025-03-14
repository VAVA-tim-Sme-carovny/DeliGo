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
    private static final int PORT = 8080;

    public static void startServer() throws IOException {
        if (server != null) {
            logger.warn("⚠️ REST API is already running on port {}", PORT);
            return;
        }

        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/health", new HealthCheckController());
        server.setExecutor(null);
        server.start();

        logger.info("✅ REST API started at http://localhost:{}/api/", PORT);
    }

    public static void stopServer() {
        if (server != null) {
            server.stop(0);
            logger.info("🛑 REST API server stopped.");
            server = null;
        } else {
            logger.warn("⚠️ No running REST API to stop.");
        }
    }
}