/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crudbankjfxclient.view;

import clientside.controller.CustomerManager;
import clientside.model.Customer;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author javi
 */
public class CustomerDataController {
    @FXML
    private Button btSearch;
    @FXML
    private Button btExit;
    @FXML
    private TextField tfCustomerID;
    @FXML
    private TextField tfFirstName;
    @FXML
    private TextField tfLastName;
    @FXML
    private TextField tfStreet;
    @FXML
    private TextField tfCity;
    @FXML
    private TextField tfZIP;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhone;
  
    private CustomerManager manager;
    
    private Stage stage;

    private static final Logger LOGGER=Logger.getLogger("crudbankjfxclient.view");

    public void setManager(CustomerManager manager) {
        this.manager=manager;
    }
    /**
     * @param stage the stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void initStage(Parent root){
        try{
            LOGGER.info("Initializing Customer data window.");
            //set scene and view DOM root
            Scene scene = new Scene(root);
            stage.setScene(scene);
            //Establecer el título de la ventana con el valor “Customer personal data”.
            stage.setTitle("Customer personal data");
            //La ventana no debe ser redimensionable.
            stage.setResizable(false);
            //El campo CustomerID debe estar habilitado y vacío.
            tfCustomerID.setDisable(false);
            tfCustomerID.clear();
            //El resto de campos de la ventana deben estar vacíos y deshabilitados
            tfFirstName.setDisable(true);
            tfFirstName.clear();
            tfLastName.setDisable(true);
            tfLastName.clear();
            tfStreet.setDisable(true);
            tfStreet.clear();
            tfCity.setDisable(true);
            tfCity.clear();
            tfZIP.setDisable(true);
            tfZIP.clear();
            tfEmail.setDisable(true);
            tfEmail.clear();
            tfPhone.setDisable(true);
            tfPhone.clear();
            //El foco debe estar en el campo CustomerID.
            tfCustomerID.requestFocus();
            //set event handlers
            tfCustomerID.textProperty().addListener(this::handleTextChanged);
            btSearch.setOnAction(this::handleOnActionSearch);
            btExit.setOnAction(this::handleOnActionExit);
            stage.setOnCloseRequest(this::handleOnActionExit);
            //show window
            stage.show();
            LOGGER.info("Customer data window initialized.");
        }catch(Exception e){
            String errorMsg="Error opening window:\n" +e.getMessage();    
            this.showErrorAlert(errorMsg);
            LOGGER.log(Level.SEVERE,errorMsg);            
        }    
    }

    private void handleTextChanged(ObservableValue observable,
             String oldValue,
             String newValue) {
        //Borrar el contenido de todos los campos de texto  excepto CustomerID.
        tfFirstName.clear();
        tfLastName.clear();
        tfStreet.clear();
        tfCity.clear();
        tfZIP.clear();
        tfEmail.clear();
        tfPhone.clear();
    }
    
    public void handleOnActionSearch(Event event){
        try{
            //Validar que el campo CustomerID contiene un valor java.lang.Long positivo :
            Long customerID=new Long(tfCustomerID.getText().trim()); 
            if (customerID<=0) throw new Exception("Customer ID debe ser mayor de 0.");
            //Si es válido, llamar al método getCustomerAccountsFullInfo de la 
            //interfaz CustomerManager para obtener un objeto Customer con los 
            //datos del cliente. 
            Customer customer=manager.getCustomerAccountsFullInfo(customerID);
            //Mostrar los datos del cliente en los campos 
            //correspondientes de la ventana.
            tfFirstName.setText(customer.getFirstName());
            tfLastName.setText(customer.getLastName());
            tfStreet.setText(customer.getStreet());
            tfCity.setText(customer.getCity());
            tfZIP.setText(customer.getZip().toString());
            tfEmail.setText(customer.getEmail());
            tfPhone.setText(customer.getPhone().toString());
        }catch(Exception e){
            //Si se produce alguna excepción  atraparla y mostrar un mensaje de 
            //alerta con el mensaje de la excepción y enfocar el campo CustomerID.
            String errorMsg="ID no valido:\n"+e.getMessage();    
            this.showErrorAlert(errorMsg);
            tfCustomerID.requestFocus();
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

    protected void showErrorAlert(String errorMsg){
        //Shows error dialog.
        Alert alert=new Alert(Alert.AlertType.ERROR,
                              errorMsg,
                              ButtonType.OK);
        alert.showAndWait();
        
    }
    
}
