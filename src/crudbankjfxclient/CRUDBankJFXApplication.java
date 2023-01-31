/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crudbankjfxclient;

import clientside.controller.CustomerManager;
import clientside.controller.CustomerManagerFactory;
import crudbankjfxclient.view.DepositsAndPaymentsController;
import crudbankjfxclient.view.ClientAccountsController;
import crudbankjfxclient.view.CustomerDataController;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 *
 * @author javi
 */
public class CRUDBankJFXApplication extends Application {
    //Get URI from properties' values file.
    private static final String CUSTOMER_ID = 
            ResourceBundle.getBundle("crudbankjfxclient.config.parameters")
                          .getString("CUSTOMER_ID");
    //Get URI from properties' values file.
    private static final String SERVER_NAME = 
            ResourceBundle.getBundle("crudbankjfxclient.config.parameters")
                          .getString("SERVER_NAME");    
    @Override
    public void start(Stage stage) throws Exception {
        //Get CustomerManager
        CustomerManager manager=CustomerManagerFactory.getCustomerManager();
        //set server name
        manager.setServerName(SERVER_NAME);
        //Load view
        FXMLLoader loader=
                new FXMLLoader(getClass().getResource("/crudbankjfxclient/view/DepositsAndPaymentsView.fxml"));
        Parent root = loader.load();
        //Get view controller and set manager for the controller in order to use it
        DepositsAndPaymentsController controller=
                (DepositsAndPaymentsController)loader.getController();
        controller.setManager(manager);
        controller.setStage(stage);
        //set customer 
        Long customerId=new Long(CUSTOMER_ID);
        controller.setCustomerId(customerId);
        controller.initStage(root);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
