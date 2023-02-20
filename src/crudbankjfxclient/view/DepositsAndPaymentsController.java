
package crudbankjfxclient.view;

import clientside.model.Account;
import clientside.model.Customer;
import clientside.controller.CustomerManager;
import clientside.model.AccountType;
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
import javafx.util.Callback;

/**
 * Banking application deposits and payments window controller. It contains window's 
 * controls event handlers for designed behavior.
 * @author Javier Martin Uria
 */
public class DepositsAndPaymentsController {
    /**
     * Label for customer identification field.
     */
    @FXML
    private Label lbCustomer;
    /**
     * Combo for customer's accounts.
     */
    @FXML
    private ComboBox cbAccount;
    /**
     * Combo for movement's operation type: deposit or payment.
     */
    @FXML
    private ComboBox cbOperation;
    /**
     * Entry field to enter amount for the movement.
     */
    @FXML
    private TextField tfAmount;
    /**
     * Action control to make the movement.
     */
    @FXML
    private Button btMake;
    /**
     * Table showing all movements made to the selected account.
     */
    @FXML
    private TableView tbMovements;
    /**
     * Table column for movement's date.
     */
    @FXML
    private TableColumn tcDate;
    /**
     * Table column for movement's type.
     */
    @FXML
    private TableColumn tcDescription;
    /**
     * Table column for movement's amount.
     */
    @FXML
    private TableColumn tcAmount;
    /**
     * A button to exit application.
     */
    @FXML
    private Button btExit;
    /**
     * Output field showing balance for selected account.
     */
    @FXML
    private TextField tfAccountBalance;
    /**
     * Business logic object. 
     */
    private CustomerManager manager;
    /**
     * Customer's id for operations.
     */
    private Long customerId;
    /**
     * Primary application stage.
     */
    private Stage stage;
    /**
     * Package logger.
     */
    private final Logger LOGGER=Logger.getLogger("crudbankjfxclient.view");
    /**
     * Business logic object setter.
     * @param manager the business object passed from application's class.
     */
    public void setManager(CustomerManager manager) {
        this.manager=manager;
    }
    /**
     * Customer id setter.
     * @param customerId the customerId to set
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    /**
     * Stage setter.
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    /**
     * Initializing method for the vindow.
     * @param root FXML document graph.
     */
    public void initStage(Parent root){
        try{
            LOGGER.info("Initializing deposits and payments window.");
            //set scene and view DOM root
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Deposits & Payments");
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
            //set total balance for selected account
            tfAccountBalance.setText(
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
            //align amounts
            tfAmount.setStyle("-fx-alignment: center-right;");
            tcAmount.setStyle("-fx-alignment: center-right;");
            tfAccountBalance.setStyle("-fx-alignment: center-right;");
            //set data for table view
            List<Movement> movements=((Account)cbAccount.getSelectionModel()
                                    .getSelectedItem()).getMovements();
            if(movements!=null)
                tbMovements.setItems(FXCollections.observableList(movements));
            //load operations on combo and select first
            ArrayList<String> operations=new ArrayList<>();
            operations.add("Deposit");
            operations.add("Payment");
            cbOperation.setItems(FXCollections.observableList(operations));
            cbOperation.getSelectionModel().selectFirst();
            //set default text for Make button
            btMake.setText("Make Deposit");
            //disable account balance
            tfAccountBalance.setDisable(true);
            //set event handlers
            btExit.setOnAction(this::handleOnActionExit);
            cbAccount.getSelectionModel().selectedItemProperty()
                     .addListener(this::handleOnSelectAccount);
            cbOperation.getSelectionModel().selectedItemProperty()
                     .addListener(this::handleOnSelectOperation);
            btMake.setOnAction(this::handleOnActionMake);
            stage.setOnCloseRequest(this::handleOnActionExit);
            //show window
            stage.show();
            LOGGER.info("Bank deposits and payments window initialized.");
        }catch(Exception e){
            String errorMsg="Error opening window:\n" +e.getMessage();    
            this.showErrorAlert(errorMsg);
            LOGGER.log(Level.SEVERE,errorMsg);            
        }    
    }
    /**
     * Accounts combo selected item property change event handler.
     * @param observable the selected item property from the combo's selectionModel.
     * @param oldValue the account previously selected.
     * @param newValue the new account selected.
     */
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
                tfAccountBalance.setText(account.getBalance().toString());
                //clear amount for operation
                tfAmount.clear();
                //Select deposit on operation combo
                cbOperation.getSelectionModel().selectFirst();
            }
        }catch(Exception e){
            String errorMsg="Error getting account data:" +e.getMessage();    
            this.showErrorAlert(errorMsg);
            LOGGER.log(Level.SEVERE,errorMsg);            
        } 
    }
    /**
     * Operations combo selected item property change event handler.
     * @param observable the selected item property from the combo's selectionModel.
     * @param oldValue the movement type previously selected.
     * @param newValue the new movement type selected.
     */
    public void handleOnSelectOperation(ObservableValue<Object> observable,
                                        Object oldValue,Object newValue){
        try{
            if(newValue.equals("Payment")) this.btMake.setText("Make Payment");
            else this.btMake.setText("Make Deposit");
        }catch(Exception e){
            String errorMsg="Error changing operation:" +e.getMessage();    
            this.showErrorAlert(errorMsg);
            LOGGER.log(Level.SEVERE,errorMsg);            
        } 
    }
    /**
     * Make button action event handler.
     * @param event An ActionEvent object.
     */
    public void handleOnActionMake(Event event){
        try{
            //Ask user for confirmation on exit
            Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
                                "¿Are you sure you want to make this operation?",
                                ButtonType.OK,ButtonType.CANCEL);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
            //If OK
                //Validate Amount is not empty
                if(tfAmount.getText().trim().isEmpty())
                    throw new Exception("Amount is required to do this operation");
                //Validate Amount is Double and positive
                Double amount=new Double(tfAmount.getText().trim());
                if (amount <= 0.0d) throw new Exception("Amount must be greater than 0.0!!");
                Account selectedAccount=(Account)cbAccount.getSelectionModel()
                                                           .getSelectedItem();
                //calculate updatedAccountBalance
                Double updatedAccountBalance;
                if(cbOperation.getSelectionModel().getSelectedItem().equals("Payment"))
                    updatedAccountBalance=selectedAccount.getBalance()-amount;
                else
                    updatedAccountBalance=selectedAccount.getBalance()+amount;
                //Validate account if balance is sufficient for payment amount
                if(selectedAccount.getType().equals(AccountType.CREDIT)){
                    //for credit accounts amount must be lower or equals to balance+ credit line
                    if (updatedAccountBalance+selectedAccount.getCreditLine()<0.0d)
                        throw new Exception("Insufficient balance and credit for this amount.");
                }else{
                    //for standar accounts amount must be lower or equals to balance
                    if (updatedAccountBalance<0.0d)
                        throw new Exception("Insufficient balance for this amount.");
                }
                //create a Movement object to encapsulate movement
                Movement movement=new Movement();
                //set amount
                movement.setAmount(amount);
                //set description from operation type
                movement.setDescription(cbOperation.getSelectionModel()
                                                   .getSelectedItem().toString());
                //set today's as time stamp for the movement
                movement.setTimestamp(new java.util.Date());
                //set updated account's balance on movement's balance
                movement.setBalance(updatedAccountBalance);
                //and call logic tier for making the movement
                manager.createMovementForCustomerAccount(movement,selectedAccount.getId());
                //update account balance 
                selectedAccount.setBalance(updatedAccountBalance);
                //refresh movements table getting customer's account data from server
                //side and firing cbAccount.selection with previously selected account
                Customer customer=manager.getCustomerAccountsFullInfo(customerId);
                List<Account> accounts=customer.getAccounts();
                cbAccount.setItems(FXCollections.observableList(accounts));
                cbAccount.getSelectionModel().select(selectedAccount);
                LOGGER.info("Movement made on account.");

            }
        }catch(NumberFormatException e){
            //This exception is thrown when constructing Double number form Amount field
            String errorMsg="Amount must be a real positive number!!";    
            this.showErrorAlert(errorMsg);
            LOGGER.severe(errorMsg);            
        }catch(Exception e){
            //Default exception handler
            String errorMsg="Error making operation:\n" +e.getMessage();    
            this.showErrorAlert(errorMsg);
            LOGGER.severe(errorMsg);            
        }
    }   
    /**
     * Exit button event handler.
     * @param event An ActionEvent object.
     */
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
    /**
     * Utility method for showing messages.
     * @param errorMsg The message to be shown.
     */
    protected void showErrorAlert(String errorMsg){
        //Shows error dialog.
        Alert alert=new Alert(Alert.AlertType.ERROR,
                              errorMsg,
                              ButtonType.OK);
        alert.showAndWait();
        
    }
}
