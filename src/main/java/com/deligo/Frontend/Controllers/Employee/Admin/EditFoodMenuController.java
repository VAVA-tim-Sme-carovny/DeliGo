package com.deligo.Frontend.Controllers.Employee.Admin;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Frontend.Controllers.InitializableWithParent;
import com.deligo.Frontend.Controllers.MainPage.MainPageController;
import com.deligo.Logging.Adapter.LoggingAdapter;
import com.deligo.Model.BasicModels.LogPriority;
import com.deligo.Model.BasicModels.LogSource;
import com.deligo.Model.BasicModels.LogType;
import com.deligo.Model.Category;
import com.deligo.Model.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

public class EditFoodMenuController implements InitializableWithParent {

    @FXML private TableView<MenuItem> menuItemTable;
    @FXML private TableColumn<MenuItem, Integer> idColumn;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, String> descriptionColumn;
    @FXML private TableColumn<MenuItem, Double> priceColumn;
    @FXML private TableColumn<MenuItem, Integer> availableColumn;

    @FXML private ListView<String> categoryListView;

    @FXML private Button addItemBtn;
    @FXML private Button editItemBtn;
    @FXML private Button deleteItemBtn;
    @FXML private Button addCategoryBtn;
    @FXML private Button editCategoryBtn;
    @FXML private Button deleteCategoryBtn;

    // Item dialog components
    @FXML private StackPane dialogContainer;
    @FXML private VBox itemDialog;
    @FXML private Label dialogTitle;
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextArea detailsField;
    @FXML private TextField priceField;
    @FXML private TextField availableCountField;
    @FXML private ComboBox<String> categoriesComboBox;
    @FXML private Button cancelBtn;
    @FXML private Button saveBtn;

    // Category dialog components
    @FXML private StackPane categoryDialogContainer;
    @FXML private VBox categoryDialog;
    @FXML private Label categoryDialogTitle;
    @FXML private TextField categoryNameField;
    @FXML private Button categoryCancelBtn;
    @FXML private Button categorySaveBtn;

    private LoggingAdapter logger;
    private MainPageController mainPageController;
    private final Gson gson = new Gson();

    private MenuItem currentItem;
    private String currentCategory;
    private boolean isNewItem = false;
    private boolean isNewCategory = false;

    private ObservableList<MenuItem> menuItems = FXCollections.observableArrayList();
    private ObservableList<String> categories = FXCollections.observableArrayList();

    private final GenericDAO<Category> categoryDAO = new GenericDAO<>(Category.class, "categories");
    private final GenericDAO<MenuItem> menuItemDAO = new GenericDAO<>(MenuItem.class, "menu_items");

    public EditFoodMenuController(LoggingAdapter logger, MainPageController mainPageController) {
        this.logger = logger;
        this.mainPageController = mainPageController;
    }

    @Override
    public void initializeWithParent(Object parentController) {
        if (parentController instanceof MainPageController) {
            this.mainPageController = (MainPageController) parentController;
        }

        // Initialize table columns
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        availableColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getAvailableCount()).asObject());

        // Load data
        loadCategories();

        // Set up category selection listener
        categoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadMenuItemsByCategory(newVal);
            }
        });

        // Set up button handlers
        editItemBtn.setDisable(true);
        deleteItemBtn.setDisable(true);
        editCategoryBtn.setDisable(true);
        deleteCategoryBtn.setDisable(true);

        menuItemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editItemBtn.setDisable(newVal == null);
            deleteItemBtn.setDisable(newVal == null);
        });

        categoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            editCategoryBtn.setDisable(newVal == null);
            deleteCategoryBtn.setDisable(newVal == null);
        });
    }

    private void loadCategories() {
        try {
            List<Category> categoryList = categoryDAO.getAll();
            List<String> categoryNames = new ArrayList<>();

            for (Category category : categoryList) {
                if (category != null && category.getName() != null) {
                    categoryNames.add(category.getName());
                }
            }

            categories = FXCollections.observableArrayList(categoryNames);
            categoryListView.setItems(categories);

            if (!categories.isEmpty()) {
                categoryListView.getSelectionModel().select(0);
            }

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, 
                    "Loaded " + categories.size() + " categories");
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                    "Error loading categories: " + e.getMessage());
            e.printStackTrace(); // Add stack trace for better debugging
        }
    }

    private void loadMenuItemsByCategory(String category) {
        try {
            if (category == null || category.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                        "Invalid category: null or empty");
                return;
            }

            // Find the category ID by name
            List<Category> categories = categoryDAO.findByField("name", category);
            if (categories == null || categories.isEmpty()) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                        "Category not found: " + category);
                return;
            }

            Category categoryObj = categories.get(0);
            if (categoryObj == null) {
                logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                        "Category object is null for: " + category);
                return;
            }

            int categoryId = categoryObj.getId();

            // Get menu items by category ID
            List<MenuItem> itemsList = menuItemDAO.findByField("category_id", categoryId);
            if (itemsList == null) {
                itemsList = new ArrayList<>();
            }

            // For backward compatibility, set the categories list for each item
            for (MenuItem item : itemsList) {
                if (item != null) {
                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add(category);
                    item.setCategories(categoryNames);
                }
            }

            menuItems = FXCollections.observableArrayList(itemsList);
            menuItemTable.setItems(menuItems);

            logger.log(LogType.INFO, LogPriority.LOW, LogSource.FRONTEND, 
                    "Loaded " + menuItems.size() + " items for category " + category);
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                    "Error loading menu items: " + e.getMessage());
            e.printStackTrace(); // Add stack trace for better debugging
        }
    }

    @FXML
    private void handleAddItem() {
        isNewItem = true;
        currentItem = new MenuItem();

        // Clear fields
        nameField.clear();
        descriptionField.clear();
        detailsField.clear();
        priceField.clear();
        availableCountField.clear();

        // Set up categories combo box
        categoriesComboBox.setItems(categories);

        // Show dialog
        dialogTitle.setText("Add New Menu Item");
        dialogContainer.setVisible(true);
    }

    @FXML
    private void handleEditItem() {
        isNewItem = false;
        currentItem = menuItemTable.getSelectionModel().getSelectedItem();

        if (currentItem != null) {
            // Populate fields
            nameField.setText(currentItem.getName());
            descriptionField.setText(currentItem.getDescription());
            detailsField.setText(currentItem.getDetails());
            priceField.setText(String.valueOf(currentItem.getPrice()));
            availableCountField.setText(String.valueOf(currentItem.getAvailableCount()));

            // Set up categories combo box
            categoriesComboBox.setItems(categories);
            if (currentItem.getCategories() != null && !currentItem.getCategories().isEmpty()) {
                categoriesComboBox.setValue(currentItem.getCategories().get(0));
            }

            // Show dialog
            dialogTitle.setText("Edit Menu Item");
            dialogContainer.setVisible(true);
        }
    }

    @FXML
    private void handleDeleteItem() {
        MenuItem item = menuItemTable.getSelectionModel().getSelectedItem();

        if (item != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Menu Item");
            alert.setContentText("Are you sure you want to delete the item: " + item.getName() + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    JSONObject requestData = new JSONObject();
                    requestData.put("itemId", item.getId());
                    requestData.put("name", item.getName());

                    String response = mainPageController.getServer().sendPostRequest(
                            "/be/menu/delItem", requestData.toString());
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.getInt("status") == 200) {
                        logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.FRONTEND, 
                                "Item deleted successfully");

                        // Refresh the list
                        String category = categoryListView.getSelectionModel().getSelectedItem();
                        if (category != null) {
                            loadMenuItemsByCategory(category);
                        }
                    } else {
                        logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                                "Failed to delete item: " + jsonResponse.getString("message"));

                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Delete Failed");
                        errorAlert.setContentText(jsonResponse.getString("message"));
                        errorAlert.showAndWait();
                    }
                } catch (Exception e) {
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                            "Error deleting item: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleAddCategory() {
        isNewCategory = true;
        currentCategory = null;

        // Clear field
        categoryNameField.clear();

        // Show dialog
        categoryDialogTitle.setText("Add New Category");
        categoryDialogContainer.setVisible(true);
    }

    @FXML
    private void handleEditCategory() {
        isNewCategory = false;
        currentCategory = categoryListView.getSelectionModel().getSelectedItem();

        if (currentCategory != null) {
            // Populate field
            categoryNameField.setText(currentCategory);

            // Show dialog
            categoryDialogTitle.setText("Edit Category");
            categoryDialogContainer.setVisible(true);
        }
    }

    @FXML
    private void handleDeleteCategory() {
        String category = categoryListView.getSelectionModel().getSelectedItem();

        if (category != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Category");
            alert.setContentText("Are you sure you want to delete the category: " + category + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    JSONObject requestData = new JSONObject();
                    requestData.put("name", category);

                    String response = mainPageController.getServer().sendPostRequest(
                            "/be/menu/delCategory", requestData.toString());
                    JSONObject jsonResponse = new JSONObject(response);

                    if (jsonResponse.getInt("status") == 200) {
                        logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.FRONTEND, 
                                "Category deleted successfully");

                        // Refresh the list
                        loadCategories();
                    } else {
                        logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                                "Failed to delete category: " + jsonResponse.getString("message"));

                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error");
                        errorAlert.setHeaderText("Delete Failed");
                        errorAlert.setContentText(jsonResponse.getString("message"));
                        errorAlert.showAndWait();
                    }
                } catch (Exception e) {
                    logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                            "Error deleting category: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleCancelDialog() {
        dialogContainer.setVisible(false);
    }

    @FXML
    private void handleSaveDialog() {
        try {
            // Validate input
            String name = nameField.getText().trim();
            String description = descriptionField.getText().trim();
            String details = detailsField.getText().trim();
            String priceText = priceField.getText().trim();
            String availableCountText = availableCountField.getText().trim();
            String category = categoriesComboBox.getValue();

            if (name.isEmpty() || details.isEmpty() || priceText.isEmpty() || category == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Name, details, price, and category are required fields.");
                alert.showAndWait();
                return;
            }

            double price;
            int availableCount;

            try {
                price = Double.parseDouble(priceText);
                availableCount = availableCountText.isEmpty() ? 0 : Integer.parseInt(availableCountText);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Price and available count must be valid numbers.");
                alert.showAndWait();
                return;
            }

            // Update current item
            currentItem.setName(name);
            currentItem.setDescription(description);
            currentItem.setDetails(details);
            currentItem.setPrice(price);
            currentItem.setAvailableCount(availableCount);

            List<String> categories = new ArrayList<>();
            categories.add(category);
            currentItem.setCategories(categories);

            // Send to backend
            if (isNewItem) {
                String itemJson = gson.toJson(currentItem);
                String response = mainPageController.getServer().sendPostRequest(
                        "/be/menu/addItem", itemJson);
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("status") == 200) {
                    logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Item added successfully");
                } else {
                    logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Failed to add item: " + jsonResponse.getString("message"));

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Add Failed");
                    alert.setContentText(jsonResponse.getString("message"));
                    alert.showAndWait();
                    return;
                }
            } else {
                JSONObject requestData = new JSONObject();
                requestData.put("itemId", currentItem.getId());
                requestData.put("name", name);
                requestData.put("description", description);
                requestData.put("details", details);
                requestData.put("price", price);
                requestData.put("availableCount", availableCount);

                JSONArray categoriesArray = new JSONArray();
                categoriesArray.put(category);
                requestData.put("categories", categoriesArray);

                String response = mainPageController.getServer().sendPostRequest(
                        "/be/menu/editItem", requestData.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("status") == 200) {
                    logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Item updated successfully");
                } else {
                    logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Failed to update item: " + jsonResponse.getString("message"));

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Update Failed");
                    alert.setContentText(jsonResponse.getString("message"));
                    alert.showAndWait();
                    return;
                }
            }

            // Hide dialog and refresh
            dialogContainer.setVisible(false);

            String selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
            if (selectedCategory != null) {
                loadMenuItemsByCategory(selectedCategory);
            }
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                    "Error saving item: " + e.getMessage());
        }
    }

    @FXML
    private void handleCategoryCancelDialog() {
        categoryDialogContainer.setVisible(false);
    }

    @FXML
    private void handleCategorySaveDialog() {
        try {
            // Validate input
            String name = categoryNameField.getText().trim();

            if (name.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Category name is required.");
                alert.showAndWait();
                return;
            }

            // Send to backend
            if (isNewCategory) {
                JSONObject requestData = new JSONObject();
                requestData.put("name", name);

                String response = mainPageController.getServer().sendPostRequest(
                        "/be/menu/addCategory", requestData.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("status") == 200) {
                    logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Category added successfully");
                } else {
                    logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Failed to add category: " + jsonResponse.getString("message"));

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Add Failed");
                    alert.setContentText(jsonResponse.getString("message"));
                    alert.showAndWait();
                    return;
                }
            } else {
                JSONObject requestData = new JSONObject();
                requestData.put("prevName", currentCategory);
                requestData.put("newName", name);

                String response = mainPageController.getServer().sendPostRequest(
                        "/be/menu/updateCategory", requestData.toString());
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getInt("status") == 200) {
                    logger.log(LogType.SUCCESS, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Category updated successfully");
                } else {
                    logger.log(LogType.ERROR, LogPriority.MIDDLE, LogSource.FRONTEND, 
                            "Failed to update category: " + jsonResponse.getString("message"));

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Update Failed");
                    alert.setContentText(jsonResponse.getString("message"));
                    alert.showAndWait();
                    return;
                }
            }

            // Hide dialog and refresh
            categoryDialogContainer.setVisible(false);
            loadCategories();
        } catch (Exception e) {
            logger.log(LogType.ERROR, LogPriority.HIGH, LogSource.FRONTEND, 
                    "Error saving category: " + e.getMessage());
        }
    }
}
