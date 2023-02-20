package crudbankjfxclient;

import clientside.controller.CustomerManager;
import clientside.controller.CustomerManagerFactory;
import crudbankjfxclient.view.DepositsAndPaymentsController;
import crudbankjfxclient.view.ClientAccountsController;
import crudbankjfxclient.view.CustomerDataController;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Application's main class. This class reads a properties file and gets necessary 
 * business objects for windows controllers. 
 * @author Javier Martin Uria
 */
public class CRUDBankJFXApplication extends Application {
    /**
     * Get URI from properties' values file.
     */
    private static final String CUSTOMER_ID = 
            ResourceBundle.getBundle("crudbankjfxclient.config.parameters")
                          .getString("CUSTOMER_ID");
    /**
     * Get URI from properties' values file.
     */
    private static final String SERVER_NAME = 
            ResourceBundle.getBundle("crudbankjfxclient.config.parameters")
                          .getString("SERVER_NAME");
    /**
     * JavaFX start method. It loads the primary window fxml and calls its controller.
     * @param stage The primary stage.
     */                      
    @Override
    public void start(Stage stage){
        try{
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
        }catch(Exception e){
            Logger.getLogger("crudbankjfxclient").severe(e.getLocalizedMessage());
            new Alert(Alert.AlertType.ERROR,e.getLocalizedMessage(),ButtonType.OK)
                      .showAndWait();
        }
    }
    /**
     * Application's entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
