package com.deligo.Backend.FeatureReview;

import com.deligo.Backend.BaseFeature.BaseFeature;
import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.ConfigLoader.ConfigLoader;
import com.deligo.Model.Response;
import com.deligo.Model.Review;
import com.deligo.RestApi.RestAPIServer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * FeatureReview implementuje funkcionalitu pre správu recenzií.
 * Poskytuje metódy na získanie, pridanie a aktualizáciu recenzií.
 */


public class FeatureReview extends BaseFeature {
private final Gson gson;
private final GenericDAO<Review> reviewDAO;

    public FeatureReview(ConfigLoader globalConfig, LoggingAdapter logger, RestAPIServer restApiServer) {
        super(globalConfig, logger, restApiServer);
        this.gson = new Gson();
        this.reviewDAO = new GenericDAO<>(Review.class, "ratings");
    }


    public String addReview(String json) {
        try {
            Review review = gson.fromJson(json, Review.class);
            
            // Validácia požiadavky pre pridanie recenzie
            if (review.getUserId() <= 0 || review.getMenuItemId() <= 0 || review.getRating() < 1 || review.getRating() > 5 || review.getComment() == null || review.getComment().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Invalid request format", 400));
            }

            // Ak to je správne tak nastaví čas
            review.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            int id = reviewDAO.insert(review);

            // Ak id je väčšie ako 0, znamená to, že recenzia bola úspešne pridaná
            if (id > 0) {
                logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.REVIEW_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Item added successfully", 200));
            } // Ak nie tak vráti to chybu
            else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.REVIEW_FAILED.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Error adding item", 500));
            }

        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            // Iné chyby
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_SUCCESS.getMessage(this.getLanguage()) + ": " + e.getMessage());
            return gson.toJson(new Response("Error adding item: " + e.getMessage(), 500));
        }
    }

    public String removeReview(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer reviewId = requestData.get("reviewId");

            if (reviewId == null || reviewId <= 0 || reviewId.toString().isEmpty()) {
                return gson.toJson(new Response("Invalid review ID", 400));
            }

            reviewDAO.delete(reviewId);
            return gson.toJson(new Response("Review deleted successfully", 200));

        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error deleting review: " + e.getMessage());
            return gson.toJson(new Response("Error deleting review", 500));
        }
    }

    public String updateReview(String json) {
        try {
            // Deserializácia JSON na objekt Review
            Review updatedReview = gson.fromJson(json, Review.class);

            // Validácia požiadavky pre aktualizáciu recenzie
            if (updatedReview.getId() <= 0 || updatedReview.getUserId() <= 0 || 
                updatedReview.getMenuItemId() <= 0 || updatedReview.getRating() < 1 || 
                updatedReview.getRating() > 5 || updatedReview.getComment() == null || 
                updatedReview.getComment().isEmpty()) {
                
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response("Invalid review data", 400));
            }
            
            // Zmení čas na aktuálny čas
            updatedReview.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Aktualizuje recenziu
            reviewDAO.update(updatedReview.getId(), updatedReview);

            // Ak to je správne tak sa to logguje
            return gson.toJson(new Response("Review updated successfully", 200));
            
        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error updating review: " + e.getMessage());
            return gson.toJson(new Response("Error updating review: " + e.getMessage(), 500));
        }
    }

    public String getReviewsByMenuItem(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer menuItemId = requestData.get("menuItemId");

            if (menuItemId == null || menuItemId <= 0 || menuItemId.toString().isEmpty()) {
                return gson.toJson(new Response("Invalid menu item ID", 400));
            }

            List<Review> reviews = reviewDAO.findByField("menu_item_id", menuItemId.toString());
            return gson.toJson(new Response(gson.toJson(reviews), 200));

        } catch (JsonSyntaxException e) {
            // Chyba pri parsovaní JSON
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error retrieving reviews: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving reviews", 500));
        }
    }
    
    public String getUserReviews(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer userId = requestData.get("userId");

            if (userId == null || userId <= 0 || userId.toString().isEmpty()) {
                return gson.toJson(new Response("Invalid user ID", 400));
            }

            List<Review> reviews = reviewDAO.findByField("user_id", userId.toString());
            return gson.toJson(new Response(gson.toJson(reviews), 200));

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error retrieving user reviews: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving user reviews", 500));
        }
    }

    public String getReviewById(String json) {

        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer reviewId = requestData.get("reviewId");

            if (reviewId == null || reviewId <= 0) {
                return gson.toJson(new Response("Invalid review ID", 400));
            }

            Optional<Review> reviewOptional = reviewDAO.getById(reviewId);
            
            if (reviewOptional.isPresent()) {
                return gson.toJson(new Response(gson.toJson(reviewOptional.get()), 200));
            } else {
                return gson.toJson(new Response("Review not found", 404));
            }

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error retrieving review: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving review", 500));
        }
    }

    public String getAllReviews(String json) {
        try {
            List<Review> reviews = reviewDAO.getAll();
            return gson.toJson(new Response(gson.toJson(reviews), 200));
        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response("Invalid request format", 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    "Error retrieving all reviews: " + e.getMessage());
            return gson.toJson(new Response("Error retrieving all reviews", 500));
        }
    }
}