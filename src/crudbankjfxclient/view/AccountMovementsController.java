/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crudbankjfxclient.view;

import clientside.model.Account;
import clientside.model.Customer;
import clientside.controller.CustomerManager;
import clientside.model.Movement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 *
 * @author javi
 */
public class AccountMovementsController {
    @FXML
    private Button btExit;
    @FXML
    private ComboBox cbAccount;
    @FXML
    private TextField tfBalance;
    @FXML
    private TableView tbMovements;
    @FXML
    private TableColumn tcDate;
    @FXML
    private TableColumn tcDescription;
    @FXML
    private TableColumn tcAmount;
    @FXML
    private TableColumn tcBalance;
    @FXML
    private Label lbCustomer;
    
    
    private CustomerManager manager;
    
    private Long customerId;
    
    private Stage stage;

    public void setManager(CustomerManager manager) {
        this.manager=manager;
    }
    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void initStage(Parent root){
        //set scene and view DOM root
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Bank Statement");
        stage.setResizable(false);
        //get data for Customer
        Customer customer=manager.getCustomerAccountsFullInfo(customerId);
        //if customer is null
        if(customer==null){
            this.showErrorAlert("Cannot find customer with id # " +
                                                customerId.toString());
            return;
        }
        lbCustomer.setText("CUSTOMER#:"+customer.getId().toString()+" "+
                           customer.getFirstName()+" "+
                           customer.getMiddleInitial()+" "+
                           customer.getLastName());
        //set data for account combo box: these two sentences are equivalent.
        //cbAccount.setItems(FXCollections.observableList(customer.getAccounts()));
        cbAccount.setItems(FXCollections.observableList(manager.getCustomerAccountsList(customer)));
        //select first account in cbAccount
        cbAccount.getSelectionModel().selectFirst();
        //set total balance
        tfBalance.setText(
                ((Account)cbAccount.getSelectionModel().getSelectedItem())
                        .getBalance().toString());
        //set cell factory values for table columns
        tcDate.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Movement, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Movement, String> movement) {
                   SimpleStringProperty property = new SimpleStringProperty();
                   DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                   property.setValue(dateFormat.format(((Movement)movement.getValue()).getTimestamp()));
                   return property;
                }
        });
        tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tcAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        tcBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        //align amounts
        tfBalance.setStyle("-fx-alignment: center-right;");
        tcAmount.setStyle("-fx-alignment: center-right;");
        tcBalance.setStyle("-fx-alignment: center-right;");
        //set data for table view
        tbMovements.setItems(
                FXCollections.observableList(
                    manager.getAccountMovementsList(
                        (Account)cbAccount.getSelectionModel().getSelectedItem())));
        //set event handlers
        btExit.setOnAction(this::handleOnActionExit);
        cbAccount.getSelectionModel().selectedItemProperty()
                 .addListener(this::handleOnSelectAccount);
        stage.setOnCloseRequest(this::handleOnActionExit);
        //show window
        stage.show();
    }

    public void handleOnActionExit(Event event){
                //Ask user for confirmation on delete
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
                                    "¿Are you sure you want to exit?",
                                    ButtonType.OK,ButtonType.CANCEL);
        Optional<ButtonType> result = alert.showAndWait();
        //If OK to exit
        if (result.isPresent() && result.get() == ButtonType.OK)
            Platform.exit();
        else event.consume();
    }
    
    public void handleOnSelectAccount(ObservableValue<Object> observable,
                                        Object oldValue,Object newValue){
        //get Movements for selected account
        if(newValue!=null){
            Account account=(Account)newValue;
            //set data for table view
            tbMovements.setItems(
                FXCollections.observableList(
                    manager.getAccountMovementsList(account)));
            tbMovements.refresh();
            //update total balance
            tfBalance.setText(account.getBalance().toString());
        }
    }
    
    protected void showErrorAlert(String errorMsg){
        //Shows error dialog.
        Alert alert=new Alert(Alert.AlertType.ERROR,
                              errorMsg,
                              ButtonType.OK);
        alert.showAndWait();
        
    }
}
