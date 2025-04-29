package com.deligo.Backend;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.ConfigLoader.ConfigLoader;


public class BackendConfig {
    private Backend backend;
    private ConfigLoader config;

    public BackendConfig(Backend be) {
        this.backend = be;
    }

    public Object routePost(String route, String data) {
        switch (route) {
            case "/testConnection":
                return backend.getFeatureValidateTestConnection().validateTestConnection(data);
            case "/api/be/updateLanguage":
                BaseFeature.updateLanguage(config);
            case "/api/be/register":
                return backend.getFeatureUserRegister().createAccount(data);
            case "/api/be/login/customer":
                return backend.getFeatureUserLogin().loginCustomer();
            case "/api/be/login/employee":
                return backend.getFeatureUserLogin().loginEmployee(data);
            case "/api/be/logout":
                return backend.getFeatureUserLogin().logout();
            case "/api/be/order/notify":
                return backend.getFeatureOrderProcessing().notifyNewOrder(Integer.parseInt(data));
            case "/api/be/order/confirm":
                return backend.getFeatureOrderProcessing().confirmOrder(data);
            case "/api/be/order/reject":
                return backend.getFeatureOrderProcessing().rejectOrder(data);
            case "/api/be/order/item/prepared":
                return backend.getFeatureOrderProcessing().markItemAsPrepared(data);
            case "/api/be/order/item/delivered":
                return backend.getFeatureOrderProcessing().markItemAsDelivered(data);
            default:
                return "Unknown POST route: " + route;
        }
    }

    public Object routeGet(String route) {
        switch (route) {
            case "/api/be/order/items/ready":
                return backend.getFeatureOrderProcessing().getReadyItems();
            default:
                return "Unknown GET route: " + route;
        }
    }
}
