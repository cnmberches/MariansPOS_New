package marianspos_new;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MariansPOS_New extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        //This funtion is for loading the fxml file which is the design of the log in module
        Parent mainMenu = FXMLLoader.load(getClass().getResource("SignInModule.fxml"));
        Scene scene = new Scene(mainMenu);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_ANY);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    try {
                        Global.isForAdminModule = true;
                        stage.close();
                        Stage x = openModule("adminPassword.fxml", Modality.WINDOW_MODAL, "Enter password");
                        x.setOnCloseRequest(new EventHandler<WindowEvent>()
                        {
                            @Override
                            public void handle(WindowEvent event)
                            {
                                Platform.runLater(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        { 
                                            openModule("SignInModule.fxml", Modality.WINDOW_MODAL, "Marian's Point of Sales System").show();
                                        }
                                        catch (IOException ex)
                                        {
                                        }
                                    }
                                });
                            }
                        });
                        x.showAndWait();
                        ke.consume(); // <-- stops passing the event to next node
                    } catch (IOException ex) {
                        Logger.getLogger(MariansPOS_New.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                    }
                }
            }
        });
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
    
    private Stage openModule(String fxmlFile, Modality modal, String title) throws IOException
    {
        //this function is for opening a new window where its parameter include the fxml file in string, 
        //how the window will open (dialog or not),and its title 
        //fxml loader is used to get the fxml file wherein it has the codes for the design of the window
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        //this parent root is for loading all the codes for design
        Parent root = (Parent) fxmlLoader.load();
        //this stage is for creating the window
        Stage stage = new Stage();
        //this function is for how the window will open (window or dialog)
        stage.initModality(modal);
        //this sets the title seen on the upper left of the window
        stage.setTitle(title);
        //this function makes the window not resizable
        stage.setResizable(false);
        //this makes sure that size is equal to the size of window based on the code
        stage.sizeToScene();
        //this puts the fxml file design in the window 
        //this if statement is to check if the window is showned not as a dialog
        //if it is WINDOW_MODAL, the main menu or log in module will close from the screen
        Scene scene = new Scene(root);
        if(fxmlFile.equalsIgnoreCase("SignInModule.fxml"))
        {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                final KeyCombination keyComb = new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_ANY);
                public void handle(KeyEvent ke) {
                    if (keyComb.match(ke)) {
                        try {
                            Global.isForAdminModule = true;
                            stage.close();
                            Stage x = openModule("adminPassword.fxml", Modality.WINDOW_MODAL, "Enter password");
                            x.setOnCloseRequest(new EventHandler<WindowEvent>()
                            {
                                @Override
                                public void handle(WindowEvent event)
                                {
                                    Platform.runLater(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            try
                                            { 
                                                openModule("SignInModule.fxml", Modality.WINDOW_MODAL, "Marian's Point of Sales System").show();
                                            }
                                            catch (IOException ex)
                                            {
                                            }
                                        }
                                    });
                                }
                            });
                            x.showAndWait();
                            ke.consume(); // <-- stops passing the event to next node
                        } catch (IOException ex) {
                            Logger.getLogger(MariansPOS_New.class.getName()).log(Level.SEVERE, null, ex);
                            ex.printStackTrace();
                        }
                    }
                }
            });
        }
        stage.setScene(scene); 
        return stage;
    }
    
}
