package marianspos_new;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MariansPOS_New extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        //This funtion is for loading the fxml file which is the design of the log in module
        Parent mainMenu = FXMLLoader.load(getClass().getResource("SignInModule.fxml"));
        Scene scene = new Scene(mainMenu);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.setTitle("Marian's Point of Sales System");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
