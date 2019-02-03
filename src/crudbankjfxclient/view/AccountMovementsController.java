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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            LOGGER.info("Initializing Bank Statement window.");
            //set scene and view DOM root
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Bank Statement");
            stage.setResizable(false);
            //get data for Customer
            Customer customer=manager.getCustomerAccountsFullInfo(customerId);
            lbCustomer.setText("CUSTOMER#:"+customer.getId().toString()+" "+
                               customer.getFirstName()+" "+
                               customer.getMiddleInitial()+" "+
                               customer.getLastName());
            //set data for account combo box.
            List<Account> accounts=customer.getAccounts();
            //if the client has no accounts
            if(accounts==null)
                throw new NoSuchElementException("Client has no Accounts");
            cbAccount.setItems(FXCollections.observableList(accounts));
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
            List<Movement> movements=((Account)cbAccount.getSelectionModel()
                                    .getSelectedItem()).getMovements();
            if(movements!=null)
                tbMovements.setItems(FXCollections.observableList(movements));
            //set event handlers
            btExit.setOnAction(this::handleOnActionExit);
            cbAccount.getSelectionModel().selectedItemProperty()
                     .addListener(this::handleOnSelectAccount);
            stage.setOnCloseRequest(this::handleOnActionExit);
            //show window
            stage.show();
            LOGGER.info("Bank Statement window initialized.");
        }catch(Exception e){
            String errorMsg="Error opening window:\n" +e.getMessage();    
            this.showErrorAlert(errorMsg);
            LOGGER.log(Level.SEVERE,errorMsg);            
        }    
    }

    public void handleOnActionExit(Event event){
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
    
    public void handleOnSelectAccount(ObservableValue<Object> observable,
                                        Object oldValue,Object newValue){
        try{
            //get Movements for selected account
            if(newValue!=null){
                Account account=(Account)newValue;
                //set data for table view
                List<Movement> movements=account.getMovements();
                //if there are movements
                if(movements!=null)
                    tbMovements.setItems(FXCollections.observableList(movements));             
                //if there is no movements
                else tbMovements.setItems(FXCollections.observableList(new ArrayList()));
                tbMovements.refresh();
                //update total balance
                tfBalance.setText(account.getBalance().toString());
            }
        }catch(Exception e){
                    String errorMsg="Error updating account data:" +e.getMessage();    
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
