package com.deligo.Frontend.Controllers.Employee;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import com.deligo.Model.Views;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class OrdersLeftController implements InitializableWithParent {

    @FXML private VBox ordersContainer;

    private MainPageController mainPageController;
    private LoggingAdapter logger;

    private final List<Order> dummyOrders = List.of(
            new Order("Obj. 1", "pending", List.of("Burger", "Fries")),
            new Order("Obj. 2", "preparing", List.of("Pizza", "Coke")),
            new Order("Obj. 3", "ready", List.of("Salad", "Water", "Cake"))
    );


    public OrdersLeftController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }

        for (Order order : dummyOrders) {
            ordersContainer.getChildren().add(createOrderItem(order));
        }
    }

    private Node createOrderItem(Order order) {
        HBox item = new HBox();
        item.setStyle("-fx-background-color: #bbbbbb; -fx-padding: 10;");
        item.setSpacing(20);
        item.setMinHeight(50);
        item.setPrefWidth(300);

        Label name = new Label(order.name());
        Label status = new Label(switch (order.status()) {
            case "pending" -> "Čaká";
            case "preparing" -> "Pripravuje sa";
            case "ready" -> "Pripravená";
            case "done" -> "Hotovo";
            default -> "Neznámy stav";
        });

        name.setStyle("-fx-font-weight: bold;");
        HBox.setHgrow(name, javafx.scene.layout.Priority.ALWAYS);
        item.getChildren().addAll(name, status);

        // Kliknutie pre detail
        item.setOnMouseClicked(e -> {
            logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.MIDDLE, BasicModels.LogSource.FRONTEND, "Clicked on: " + order.name());
            mainPageController.loadView("/Views/Content/EmployeePanel/OrderDetailContentPanel.fxml", Views.mainContent);
        });

        return item;
    }

    // 🧾 Pomocná trieda na dummy objednávky
    private record Order(String name, String status, List<String> items) {}
}
