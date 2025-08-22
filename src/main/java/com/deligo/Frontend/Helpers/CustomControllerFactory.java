package com.deligo.Frontend.Helpers;

import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.RestApi.RestAPIServer;
import javafx.util.Callback;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class CustomControllerFactory implements Callback<Class<?>, Object> {

    private final LoggingAdapter logger;
    private final MainPageController mainPageController;
    private final RestAPIServer restApiServer;
    private final ConfigLoader config;

    public CustomControllerFactory(MainPageController mainPageController,
                                   LoggingAdapter logger,
                                   RestAPIServer restApiServer,
                                   ConfigLoader config) {
        this.logger = logger;
        this.mainPageController = mainPageController;
        this.restApiServer = restApiServer;
        this.config = config;
    }

    @Override
    public Object call(Class<?> controllerClass) {
        try {
            for (Constructor<?> constructor : controllerClass.getConstructors()) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                Object[] args = Arrays.stream(paramTypes)
                        .map(this::resolveDependency)
                        .toArray();

                if (Arrays.stream(args).noneMatch(arg -> arg == null)) {
                    return constructor.newInstance(args);
                }
            }

            return controllerClass.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create controller: " + controllerClass.getName(), e);
        }
    }

    private Object resolveDependency(Class<?> type) {
        if (type == LoggingAdapter.class) return logger;
        if (type == MainPageController.class) return mainPageController;
        if (type == RestAPIServer.class) return restApiServer;
        if (type == ConfigLoader.class) return config;
        return null;
    }
}
