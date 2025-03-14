package main.com.api.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import main.com.backend.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class RestAPIServer {
    private static final Logger logger = LogManager.getLogger(RestAPIServer.class);
    private static Config backendConfig;
    private static Config frontendConfig;

    public static void main(String[] args) throws IOException {
        // 1️⃣ Create HTTP server on localhost, port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 2️⃣ Add handlers for API requests
        server.createContext("/api", new RequestHandler());

        // 3️⃣ Start the server
        server.setExecutor(null);
        server.start();
        logger.info("REST API Server running at http://localhost:8080/api");
    }

    // Inject backend configuration
    public static void setBackendConfig(Config config) {
        backendConfig = config;
    }

    // Inject frontend configuration
    public static void setFrontendConfig(Config config) {
        frontendConfig = config;
    }

    // Handler for processing GET & POST requests
    static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String route = exchange.getRequestURI().getPath();
            String response = "";

            // 4️⃣ Process POST requests
            if ("POST".equalsIgnoreCase(method)) {
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                logger.info("Received POST: " + route + " | Data: " + requestBody);

                if (route.startsWith("/be")) {
                    response = (backendConfig != null) ? backendConfig.routePost(route, requestBody).toString() : "Backend Config not set!";
                } else if (route.startsWith("/fe")) {
                    response = (frontendConfig != null) ? frontendConfig.routePost(route, requestBody).toString() : "Frontend Config not set!";
                } else {
                    response = "Invalid API route!";
                }
            }

            // 5️⃣ Process GET requests
            else if ("GET".equalsIgnoreCase(method)) {
                logger.info("Received GET: " + route);

                if (route.startsWith("/be")) {
                    response = (backendConfig != null) ? backendConfig.routeGet(route).toString() : "Backend Config not set!";
                } else if (route.startsWith("/fe")) {
                    response = (frontendConfig != null) ? frontendConfig.routeGet(route).toString() : "Frontend Config not set!";
                } else {
                    response = "Invalid API route!";
                }
            }

            // Send response
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        }
    }
}