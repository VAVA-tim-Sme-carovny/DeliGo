package com.deligo.Frontend.Controllers.Employee;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class OrderDetailController implements InitializableWithParent {

    @FXML
    private Label orderNumberLabel;

    @FXML
    private Label tableNumberLabel;

    @FXML
    private Label totalPriceLabel;

    private MainPageController mainPageController;
    private LoggingAdapter logger;

    public OrderDetailController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }

        // Dummy data – môžeš ich nahradiť setData() funkciou
        orderNumberLabel.setText("Objednávka č. 1");
        tableNumberLabel.setText("Stôl č. 3");
        totalPriceLabel.setText("Cena: 23.50 €");
    }

    public void setPending() {
        logAndUpdateStatus("pending");
    }

    public void setPreparing() {
        logAndUpdateStatus("preparing");
    }

    public void setReady() {
        logAndUpdateStatus("ready");
    }

    public void setDone() {
        logAndUpdateStatus("done");
    }

    private void logAndUpdateStatus(String status) {
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.FRONTEND, "Zmena stavu objednávky: " + status);
        // sem môžeš doplniť aktualizáciu na server alebo zmenu v databáze
    }
}
