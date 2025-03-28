package com.deligo.RestApi.Handlers;

import com.deligo.Backend.BackendConfig;
import com.deligo.RestApi.RestAPIServer;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BackendHandler implements HttpHandler {

    private final RestAPIServer server;
    private final LoggingAdapter logger;

    public BackendHandler(RestAPIServer server) {
        this.server = server;
        this.logger = server.getLogger();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath(); // napr. "/api/be/health" alebo "/api/be/testConnection"
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "BackendHandler received request: " + method + " " + path);

        // Odstránime prefix "/api/be"
        String subRoute = path.substring("/api/be".length());
        String response;
        switch (subRoute) {
            case "/api/health":
                response = handleHealth(method);
                break;
            default:
                response = handleBackendRoute(method, subRoute, exchange);
                break;
        }

        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()){
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String handleHealth(String method) {
        if ("GET".equalsIgnoreCase(method)) {
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "GET /api/be/health checked");
            return "GET /api/be/health OK!";
        } else if ("POST".equalsIgnoreCase(method)) {
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "POST /api/be/health checked");
            return "POST /api/be/health OK!";
        } else {
            logger.log(LogType.WARNING, LogPriority.LOW, LogSource.REST_API, "Unsupported method on /api/be/health");
            return "Method not supported for /health";
        }
    }

    private String handleBackendRoute(String method, String subRoute, HttpExchange exchange) throws IOException {
        BackendConfig backendConfig = server.getBackendConfig();
        if (backendConfig == null) {
            return "Backend not configured!";
        }

        if ("POST".equalsIgnoreCase(method)) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.REST_API, "Request body: " + requestBody);
            return backendConfig.routePost(subRoute, requestBody).toString();
        } else if ("GET".equalsIgnoreCase(method)) {
            return backendConfig.routeGet(subRoute).toString();
        }
        return "Method not supported for " + subRoute;
    }
}
