package com.deligo.Frontend.Controllers.Review;

import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class ReviewController implements InitializableWithParent {

    @FXML private HBox starsContainer;
    @FXML private TextArea commentArea;
    @FXML private Label rating_star1;
    @FXML private Label rating_star2;
    @FXML private Label rating_star3;
    @FXML private Label rating_star4;
    @FXML private Label rating_star5;

    private MainPageController mainPageController;
    private LoggingAdapter logger;
    private int currentRating = 0;
    private List<Label> stars;

    public ReviewController(LoggingAdapter logger) {
        this.logger = logger;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        this.mainPageController = (MainPageController) parentController;
        stars = new ArrayList<>(List.of(rating_star1, rating_star2, rating_star3, rating_star4, rating_star5));
        
        // Set up click handlers for each star
        for (int i = 0; i < stars.size(); i++) {
            final int rating = i + 1;
            stars.get(i).setOnMouseClicked(event -> setRating(rating));
        }
    }

    private void setRating(int rating) {
        currentRating = rating;
        // Update star colors
        for (int i = 0; i < stars.size(); i++) {
            if (i < rating) {
                stars.get(i).setStyle("-fx-text-fill: #ffe100;");
            } else {
                stars.get(i).setStyle("-fx-text-fill: white;");
            }
        }
        logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, "Rating set to: " + rating);
    }

    public int getCurrentRating() {
        return currentRating;
    }

    public String getComment() {
        return commentArea.getText();
    }
} 