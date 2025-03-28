package com.deligo.RestApi.Handlers;

import com.deligo.Frontend.FrontendConfig;
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

public class FrontendHandler implements HttpHandler {

    private final RestAPIServer server;
    private final LoggingAdapter logger;

    public FrontendHandler(RestAPIServer server) {
        this.server = server;
        this.logger = server.getLogger();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath(); // napr. "/api/fe/health"
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "FrontendHandler received request: " + method + " " + path);

        // Odstránime prefix "/api/fe"
        String subRoute = path.substring("/api/fe".length());
        String response;
        switch (subRoute) {
            case "/api/health":
                response = handleHealth(method);
                break;
            default:
                response = handleFrontendRoute(method, subRoute, exchange);
                break;
        }

        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()){
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String handleHealth(String method) {
        if ("GET".equalsIgnoreCase(method)) {
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "GET /api/fe/health checked");
            return "GET /api/fe/health OK!";
        } else if ("POST".equalsIgnoreCase(method)) {
            logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.REST_API, "POST /api/fe/health checked");
            return "POST /api/fe/health OK!";
        } else {
            logger.log(LogType.WARNING, LogPriority.LOW, LogSource.REST_API, "Unsupported method on /api/fe/health");
            return "Method not supported for /health";
        }
    }

    private String handleFrontendRoute(String method, String subRoute, HttpExchange exchange) throws IOException {
        FrontendConfig frontendConfig = server.getFrontendConfig();
        if (frontendConfig == null) {
            return "Frontend not configured!";
        }

        if ("POST".equalsIgnoreCase(method)) {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.REST_API, "Request body: " + requestBody);
            return frontendConfig.routePost(subRoute, requestBody).toString();
        } else if ("GET".equalsIgnoreCase(method)) {
            return frontendConfig.routeGet(subRoute).toString();
        }
        return "Method not supported for " + subRoute;
    }
}
