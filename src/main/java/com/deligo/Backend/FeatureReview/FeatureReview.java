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
        logger.log(LogType.INFO, LogPriority.MIDDLE, LogSource.BECKEND, 
                ReviewMessages.PROCES_NAME.getMessage(this.getLanguage()));
    }

    public String addReview(String json) {
        try {
            Review review = gson.fromJson(json, Review.class);
            
            if (review.getUserId() <= 0 || review.getMenuItemId() <= 0 || review.getRating() < 1 || review.getRating() > 5 || review.getComment() == null || review.getComment().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
            }

            review.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            int id = reviewDAO.insert(review);

            if (id > 0) {
                logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.REVIEW_SUCCESS.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.REVIEW_SUCCESS.getMessage(this.getLanguage()), 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.REVIEW_FAILED.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.REVIEW_FAILED.getMessage(this.getLanguage()), 500));
            }

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_FAILED.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_FAILED.getMessage(this.getLanguage()), 500));
        }
    }

    public String removeReview(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer reviewId = requestData.get("reviewId");

            if (reviewId == null || reviewId <= 0 || reviewId.toString().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()), 400));
            }

            reviewDAO.delete(reviewId);
            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    ReviewMessages.REVIEW_DELETE_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_DELETE_SUCCESS.getMessage(this.getLanguage()), 200));

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_DELETE_ERROR.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_DELETE_ERROR.getMessage(this.getLanguage()), 500));
        }
    }

    public String updateReview(String json) {
        try {
            Review updatedReview = gson.fromJson(json, Review.class);

            if (updatedReview.getId() <= 0 || updatedReview.getUserId() <= 0 || 
                updatedReview.getMenuItemId() <= 0 || updatedReview.getRating() < 1 || 
                updatedReview.getRating() > 5 || updatedReview.getComment() == null || 
                updatedReview.getComment().isEmpty()) {
                
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
            }
            
            updatedReview.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            reviewDAO.update(updatedReview.getId(), updatedReview);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.BECKEND,
                    ReviewMessages.REVIEW_UPDATE_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_UPDATE_SUCCESS.getMessage(this.getLanguage()), 200));
            
        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_UPDATE_ERROR.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_UPDATE_ERROR.getMessage(this.getLanguage()), 500));
        }
    }

    public String getReviewsByMenuItem(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer menuItemId = requestData.get("menuItemId");

            if (menuItemId == null || menuItemId <= 0 || menuItemId.toString().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()), 400));
            }

            List<Review> reviews = reviewDAO.findByField("menu_item_id", menuItemId.toString());
            return gson.toJson(new Response(gson.toJson(reviews), 200));

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()), 500));
        }
    }
    
    public String getUserReviews(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer userId = requestData.get("userId");

            if (userId == null || userId <= 0 || userId.toString().isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()), 400));
            }

            List<Review> reviews = reviewDAO.findByField("user_id", userId.toString());
            return gson.toJson(new Response(gson.toJson(reviews), 200));

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_NOT_FOUND_FOR_USER.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_NOT_FOUND_FOR_USER.getMessage(this.getLanguage()), 500));
        }
    }

    public String getReviewById(String json) {
        try {
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> requestData = gson.fromJson(json, mapType);
            Integer reviewId = requestData.get("reviewId");

            if (reviewId == null || reviewId <= 0) {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.INVALID_REVIEW_ID.getMessage(this.getLanguage()), 400));
            }

            Optional<Review> reviewOptional = reviewDAO.getById(reviewId);
            
            if (reviewOptional.isPresent()) {
                return gson.toJson(new Response(gson.toJson(reviewOptional.get()), 200));
            } else {
                logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                        ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()));
                return gson.toJson(new Response(ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()), 404));
            }

        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()), 500));
        }
    }

    public String getAllReviews(String json) {
        try {
            List<Review> reviews = reviewDAO.getAll();
            logger.log(LogType.SUCCESS, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_SUCCESS.getMessage(this.getLanguage()));
            return gson.toJson(new Response(gson.toJson(reviews), 200));
        } catch (JsonSyntaxException e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.INVALID_REQUEST_FORMAT.getMessage(this.getLanguage()), 400));
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.BECKEND,
                    ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()));
            return gson.toJson(new Response(ReviewMessages.REVIEW_NOT_FOUND.getMessage(this.getLanguage()), 500));
        }
    }
}