/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crudbankjfxclient.view;

import clientside.model.Account;
import clientside.model.Customer;
import clientside.controller.CustomerManager;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 *
 * @author javi
 */
public class ClientAccountsController {
    @FXML
    private TextField tfCustomerId;
    @FXML
    private Button btSearch;
    @FXML
    private TableView tbAccounts;
    @FXML
    private TableColumn tcAccountNumber;
    @FXML
    private TableColumn tcType;
    @FXML
    private TableColumn tcDescription;
    @FXML
    private TableColumn tcBalance;
    @FXML
    private TextField tfTotalBalance;
    @FXML
    private Button btExit;
  
    private CustomerManager manager;
    
    private Long customerId;
    
    private Stage stage;

    private final Logger LOGGER=Logger.getLogger("crudbankjfxclient.view");

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
        try{
            LOGGER.info("Initializing Client Accounts view.");
            //set scene and view DOM root
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Customer's accounts and balances");
            stage.setResizable(false);
            //set cell factory values for table columns
            tcAccountNumber.setCellValueFactory(new PropertyValueFactory<>("id"));
            tcType.setCellValueFactory(new PropertyValueFactory<>("type"));
            tcDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            tcBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
            //align amounts
            tcAccountNumber.setStyle("-fx-alignment: center-right;");
            tcBalance.setStyle("-fx-alignment: center-right;");
            tfTotalBalance.setStyle("-fx-alignment: center-right;");
            //disable total balance
            tfTotalBalance.setDisable(true);
            //focus on CustomerId
            tfCustomerId.requestFocus();
            //set event handlers
            tfCustomerId.textProperty().addListener(this::handleCustomerIdTextChange);
            btSearch.setOnAction(this::handleSearchAction);
            btExit.setOnAction(this::handleExitAction);
            stage.setOnCloseRequest(this::handleExitAction);
            //show window
            stage.show();
            LOGGER.info("Client Accounts view initialized.");
        }catch(Exception e){
            String errorMsg="Error opening window:\n" +e.getMessage();    
            this.showErrorAlert(errorMsg);
            LOGGER.log(Level.SEVERE,errorMsg);            
        }    
    }
    
    public void handleCustomerIdTextChange(ObservableValue observable,
                                            String oldValue,
                                            String newValue){
        try{
            //clear table items and tfTotalBalance
            tbAccounts.getItems().clear();
            tbAccounts.refresh();
            tfTotalBalance.setText("");
            
        }catch(Exception e){
                    String errorMsg="Error clearing data:" +e.getMessage();    
                    this.showErrorAlert(errorMsg);
                    LOGGER.log(Level.SEVERE,errorMsg);            
        } 
    }
    
    public void handleSearchAction(ActionEvent event){
        try{
            //validate that customer id is not empty
            if(tfCustomerId.getText().trim().isEmpty())
                throw new Exception("Please enter a Customer ID");
            //validate that customer id is positive 
            customerId=new Long(tfCustomerId.getText().trim());
            if (customerId< 0)
                throw new Exception("Customer ID must be positive.");
            //get data for Customer
            Customer customer=manager.getCustomerAccountsFullInfo(customerId);
            List<Account> accounts=customer.getAccounts();
            //if the client has no accounts
            if(accounts==null)
                throw new NoSuchElementException("Client has no Accounts");
            //set data for table view
            tbAccounts.setItems(FXCollections.observableList(accounts));
            //set total balance as the sum of each account balance
            Double totalBalance= accounts.stream()
                    .mapToDouble(Account::getBalance)
                    .sum();
            tfTotalBalance.setText(totalBalance.toString());

        }catch(Exception e){
            String errorMsg;
            if (e instanceof NumberFormatException)
                errorMsg="Customer id must be numeric.";
            else     
                errorMsg=e.getMessage();
            //show a message            
            this.showErrorAlert(errorMsg);
            //focus Customer id
            tfCustomerId.requestFocus();
            LOGGER.log(Level.SEVERE,errorMsg);            
        } 
    }

    public void handleExitAction(Event event){
        try{
            //Ask user for confirmation on exit
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
                                        "¿Are you sure you want to exit?",
                                        ButtonType.OK,ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            //If OK to exit
            if (result.isPresent() && result.get() == ButtonType.OK)
                Platform.exit();
            else event.consume();
        }catch(Exception e){
                    String errorMsg="Error exiting application:" +e.getMessage();    
                    this.showErrorAlert(errorMsg);
                    LOGGER.log(Level.SEVERE,errorMsg);            
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
