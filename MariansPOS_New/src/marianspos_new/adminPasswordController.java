package marianspos_new;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class adminPasswordController implements Initializable {
    private String password;
    
    @FXML
    private Button void_btn, cancel_btn;
    
    @FXML
    private TextField password_tf;
    
    @FXML
    private void voidBtn(ActionEvent e)
    {
        if(password_tf.getText().equalsIgnoreCase(password))
        {
            if(!Global.isForAdminModule)
            {
                Global.isVoid = true;
            }
            else
            {
                
                Global.isPasswordCorrect = true;
                if(Global.isPasswordCorrect)
                {
                    try {
                        openModule("MainMenuModule.fxml", Modality.WINDOW_MODAL, "Dashboard");
                        Global.isPasswordCorrect = false;
                    } catch (IOException ex) {
                        Logger.getLogger(adminPasswordController.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                    }
                }
                Global.isForAdminModule = false;
                System.out.println("runned");
            }
            //this function is use to get the source file of the action event
            final Node source = (Node) e.getSource();
            //this gets the sctive stage or window of the file
            final Stage stage = (Stage) source.getScene().getWindow();
            //this is for closing the window
            stage.close();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please make sure that the password is correct" ,ButtonType.OK);
            alert.setHeaderText("Incorrect Password");
            alert.setTitle("");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void cancel(ActionEvent e)
    {
        //this function is use to get the source file of the action event
        final Node source = (Node) e.getSource();
        //this gets the sctive stage or window of the file
        final Stage stage = (Stage) source.getScene().getWindow();
        //this is for closing the window
        stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        if(Global.isForAdminModule)
        {
            void_btn.setText("SIGN IN");
        }
        final String queryCheck = "SELECT * from accounts_tbl WHERE role = ?";
        try
        {
            DBConnector db = new DBConnector();
            PreparedStatement ps = db.getConnection().prepareStatement(queryCheck);
            ps.setString(1, "admin");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next())
            {
                password = resultSet.getString("password");
                Global.name = resultSet.getString("name");
                Global.role = resultSet.getString("role");
                Global.account_id = resultSet.getString("accounts_id");
                Global.username = resultSet.getString("username");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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
        stage.setScene(new Scene(root));  
        if(fxmlFile.equalsIgnoreCase("MainMenuModule.fxml"))
        {
            stage.setOnCloseRequest(new EventHandler<WindowEvent>()
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
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out?" , ButtonType.NO, ButtonType.YES);
                                alert.setTitle("Log out user?");
                                //the show and wait functions waits the user to click between the buttons ok cancel
                                alert.showAndWait();
                                if(alert.getResult().equals(ButtonType.YES))
                                {
                                    stage.close();
                                    openModule("SignInModule.fxml", Modality.WINDOW_MODAL, "Marian's Point of Sales System");
                                }
                                else
                                {
                                    openModule("MainMenuModule.fxml", Modality.WINDOW_MODAL, "Dashboard");
                                }
                            }
                            catch (IOException ex)
                            {
                            }
                        }
                    });
                }
            });
        }
        //if it is WINDOW_MODAL, the main menu or log in module will close from the screen
        stage.show();
        return stage;
    }
}
