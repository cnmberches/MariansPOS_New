package marianspos_new;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VoidModuleController implements Initializable {
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
            Global.isVoid = true;
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
        final String queryCheck = "SELECT password from accounts_tbl WHERE role = ?";
        try
        {
            DBConnector db = new DBConnector();
            PreparedStatement ps = db.getConnection().prepareStatement(queryCheck);
            ps.setString(1, "admin");
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next())
            {
                password = resultSet.getString("password");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }    
    
}
