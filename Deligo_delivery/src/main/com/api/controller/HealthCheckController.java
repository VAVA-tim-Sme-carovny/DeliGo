package main.com.api.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HealthCheckController implements HttpHandler {
    private static final Logger logger = LogManager.getLogger(HealthCheckController.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "Health Check: OK";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
        logger.info("Health check request received.");
    }
}