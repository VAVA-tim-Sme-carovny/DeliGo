package com.deligo.Frontend.Controllers.OrderPage;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels;
import com.deligo.Model.Food;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderCartController implements InitializableWithParent {

    @FXML private VBox cartItemsContainer;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmBtn;

    private LoggingAdapter logger;
    private MainPageController mainPageController;

    public OrderCartController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    private final List<Food> dummyOrder = List.of(
            new Food("Burger", 4.50),
            new Food("Cheeseburger", 5.00),
            new Food("Big Mac", 6.00),
            new Food("Fries", 2.00)
    );

    private final List<CartItemController> itemControllers = new ArrayList<>();

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;

        for (Food food : dummyOrder) {
            addItemToCart(food);
        }

        if (confirmBtn != null) {
            confirmBtn.setOnAction(e -> {
                this.logger.log(BasicModels.LogType.INFO, BasicModels.LogPriority.LOW, BasicModels.LogSource.FRONTEND, "Order confirmed");
                System.out.println("✅ Objednávka potvrdená");
                // implementuj logiku odoslania neskôr
            });
        }

        recalculateTotal();
    }

    private void addItemToCart(Food food) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/Content/OrderPanel/CartItem.fxml"));
            Node node = loader.load();

            CartItemController controller = loader.getController();
            controller.setData(food.getName(), food.getPrice(), () -> {
                cartItemsContainer.getChildren().remove(node);
                itemControllers.remove(controller);
                recalculateTotal();
            }, this.logger);

            itemControllers.add(controller);
            cartItemsContainer.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recalculateTotal() {
        double total = itemControllers.stream()
                .mapToDouble(CartItemController::getPrice)
                .sum();
        totalPriceLabel.setText(String.format("Spolu: %.2f €", total));
    }
}
