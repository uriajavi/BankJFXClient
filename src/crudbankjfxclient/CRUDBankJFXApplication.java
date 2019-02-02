/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crudbankjfxclient;

import clientside.controller.CustomerManager;
import clientside.controller.CustomerManagerFactory;
import crudbankjfxclient.view.AccountMovementsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 *
 * @author javi
 */
public class CRUDBankJFXApplication extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //Get CustomerManager
        CustomerManager manager=CustomerManagerFactory.getCustomerManager();
        //Load view
        FXMLLoader loader=
                new FXMLLoader(getClass().getResource("view/AccountMovementsView.fxml"));
        Parent root = loader.load();
        //Set manager for UI controller
        AccountMovementsController controller=
                (AccountMovementsController)loader.getController();
        controller.setManager(manager);
        manager.setServerName("localhost");
        controller.setStage(stage);
        //hardcoded customer id
        Long customerId=new Long(102263301);
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
