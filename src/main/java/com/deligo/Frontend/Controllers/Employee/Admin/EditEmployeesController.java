package com.deligo.Frontend.Controllers.Employee.Admin;

import com.deligo.DatabaseManager.dao.GenericDAO;
import com.deligo.Model.Role;
import com.deligo.Model.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditEmployeesController implements Initializable {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, Role> roleColumn;

    private final GenericDAO<User> userDAO = new GenericDAO<>(User.class, "users");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(data -> data.getValue().getFxUsername());
        roleColumn.setCellValueFactory(data -> data.getValue().getFxRoles());

        roleColumn.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(Role.values())));

        roleColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            String newRole = event.getNewValue().toString();
            user.setRoles(newRole);

            JSONObject data = new JSONObject();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("roles", newRole.toLowerCase());
            data.put("tags", "");

            // zavolaj backend – predpokladáš správne
            // mainPageController.getServer().sendPostRequest(...) – doplň sem
        });

        userTable.setEditable(true);
        loadUsers();
    }

    private void loadUsers() {
        List<User> users = userDAO.getAll();
        userTable.setItems(FXCollections.observableArrayList(users));
    }
}
