package marianspos_new;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class RegisterController implements Initializable {

    private Alert alert;
    private DBConnector db = new DBConnector();
    
    @FXML
    private TextField name_textField, username_textField, password_textField, repassword_textField ;
    
    @FXML
    private RadioButton admin_rb, employee_rb;
    
    @FXML
    private ToggleGroup role_tGroup;
    
    @FXML
    private Button register_Btn;

    @FXML
    private DatePicker datePicker;
    
    @FXML
    private void registerUser(ActionEvent e){
        //This function is for adding a new account user for the system
        //First is to check if all fields are not empyt, password and retype password field 
        //text is the same, and the password must be 8 characters long
        if(isNotEmpty() && repassword_textField.getText().equals(password_textField.getText()) && is8CharactersLong()){
            
            //this gets the value or text in the date picker and converts it to a date that has no timezone
            LocalDate localDate = datePicker.getValue(); 

            //This gets the values or texts in the fields and store them to the string variable
            String name, username, password, role = "", date;
            name = name_textField.getText();
            username = username_textField.getText();
            password = repassword_textField.getText();
            //This line formats the local date into MMM-dd-YYYY pattern (Example: Oct-24-2019) and store it to string variable
            date = localDate.format(DateTimeFormatter.ofPattern("MMM-dd-yyyy",Locale.US));

            if(role_tGroup.getSelectedToggle().equals(admin_rb))
            {
                //this if else statement checks if the selected radio button is for admin or employee
                //if the selected radio button is admin, the text "admin" will be stored in the string variable
                role = "admin";
            }
            else
            {
                //if the selected radio button is employee, the text "employee" will be stored in the string variable
                role = "employee";
            }
            
            //this text is for the confirmation message before saving the data in the database
            String alertInfo = "Register this user?\n"
                + "Name: " + name + "\n"
                + "Username: " + username + "\n"
                + "Date Hired: " + date;
                
            //this alert object is for the confirmation message with the buttons ok and cancel
            alert = new Alert(AlertType.CONFIRMATION, alertInfo , ButtonType.CANCEL, ButtonType.OK);
            alert.setTitle("");
            //the show and wait functions waits the user to click between the buttons ok cancel
            alert.showAndWait();

            //this if else statement is for checkin if the user clicks ok to proceed in adding the new account
            if (alert.getResult() == ButtonType.OK)
            {
                if(Global.isForAddAccount)
                {
                    try
                    {
                        //first is to get a connection and create a statement
                        Statement st = db.getConnection().createStatement();
                        // this result set is used to get all the users account username
                        ResultSet rs = st.executeQuery("SELECT * FROM accounts_tbl where username = '"+ username +"'");
                        if(rs.next())
                        {
                            //if there is already same username, this alerts the user and do not allow to have the same username
                            alert = new Alert(AlertType.ERROR, "Username is already existing, try a new one" ,ButtonType.OK);
                            alert.setHeaderText("Username already exist");
                            alert.setTitle("");
                            alert.showAndWait();
                        }
                        else
                        {
                            //this query is for inserting the values name, username, password, role, and date_hired
                            //it uses "?" in the values for preparedstatement
                            String sql = "INSERT INTO accounts_tbl(name, username, password, role, date_hired) "
                                        + "VALUES(?, ?, ?, ?, ?)";
                            try
                            {
                                //prepared statement is used instead of statement to prevent sql injection
                                //first is to get the connection then prepare the statement query
                                PreparedStatement ps = db.getConnection().prepareStatement(sql);
                                //this inserts the data by index and its corresponding value
                                ps.setString(1, name);
                                ps.setString(2, username);
                                ps.setString(3, password);
                                ps.setString(4, role);
                                ps.setString(5, date);
                                //this function is for commanding the system to do the query which inserts a new row/data in database
                                ps.executeUpdate();
                                db.getConnection().close();
                            }catch(SQLException ex)
                            {
                                //This is for getting the error message if theres a problem in the connection or query
                                System.out.println(ex.getMessage());
                            }
                        }
                    } catch (SQLException ex) {
                        //this prints the error message if it encounters problem
                        System.out.println(ex.getMessage());
                    }
                }
                else
                {
                    try
                    {
                        //first is to get a connection and create a statement
                        Statement st = db.getConnection().createStatement();
                        //this query is for inserting the values name, username, password, role, and date_hired
                        //it uses "?" in the values for preparedstatement
                        String sql = "UPDATE accounts_tbl SET name = ?, username = ?, password = ?, role = ?, date_hired=? WHERE accounts_id = ?";
                        try
                        {
                            //prepared statement is used instead of statement to prevent sql injection
                            //first is to get the connection then prepare the statement query
                            PreparedStatement ps = db.getConnection().prepareStatement(sql);
                            //this inserts the data by index and its corresponding value
                            ps.setString(1, name);
                            ps.setString(2, username);
                            ps.setString(3, password);
                            ps.setString(4, role);
                            ps.setString(5, date);
                            ps.setInt(6, Integer.parseInt(Global.accMenuClickedItems[0]));
                            //this function is for commanding the system to do the query which inserts a new row/data in database
                            ps.executeUpdate();
                            db.getConnection().close();
                        }catch(SQLException ex)
                        {
                            //This is for getting the error message if theres a problem in the connection or query
                            System.out.println(ex.getMessage());
                        }
                    } catch (SQLException ex) {
                        //this prints the error message if it encounters problem
                        System.out.println(ex.getMessage());
                    }
                }
                
            }
            
            //this function is use to get the source file of the action event
            final Node source = (Node) e.getSource();
            //this gets the sctive stage or window of the file
            final Stage stage = (Stage) source.getScene().getWindow();
            //this is for closing the window
            stage.close();
        }
        else if(!repassword_textField.getText().equals(password_textField.getText()))
        {
            //this alert is for telling the user that password and retype password must match
            alert = new Alert(AlertType.ERROR, "Please make sure that password and re-type password match" ,ButtonType.OK);
            alert.setHeaderText("Does not match with password");
            alert.setTitle("");
            alert.showAndWait();
        }
        else if(!is8CharactersLong())
        {
            //this alert window is for telling the user to make sure that the password must not be less than 8 characters
            alert = new Alert(AlertType.ERROR, "Please make sure that the password is eight (8) characters long" ,ButtonType.OK);
            alert.setHeaderText("Short password");
            alert.setTitle("");
            alert.showAndWait();
        }
        else
        {
            //this alert is for telling the user to fill all the needed information (no blank or white space)
            alert = new Alert(AlertType.ERROR, "Please fill in all the needed information" ,ButtonType.OK);
            alert.setHeaderText("Incomplete information");
            alert.setTitle("");
            alert.showAndWait();
        }
    }
    
    private Boolean isNotEmpty()
    {
        //this checks if all the field is not empty or blank
        return !name_textField.getText().isEmpty() && !username_textField.getText().isEmpty()
           && !datePicker.getValue().toString().isEmpty() && !password_textField.getText().isEmpty()
           && !repassword_textField.getText().isEmpty();
    }
    
    private Boolean is8CharactersLong()
    {
        //this is for checking if the password is 8 characters long 
        return password_textField.getText().length() > 7;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //when the window appears to the screen, the system must check first if there is already an admin user
        //this is to prevent having multiple admin user.
        Statement st;
        
        try {
            //first is to get a connection and create a statement
            st = db.getConnection().createStatement();
            // this result set is used to get all the users that are admin
            ResultSet rs = st.executeQuery("SELECT * FROM accounts_tbl where role = 'admin'");
            if(rs.next())
            {
                //if there is already an admin, this automatically disables the admin radiobutton for creating new admin account
                admin_rb.setDisable(true);
            }
        } catch (SQLException ex) {
            //this prints the error message if it encounters problem
            System.out.println(ex.getMessage());
        }
        if(!Global.isForAddAccount)
        {
            try {
                //first is to get a connection and create a statement
                st = db.getConnection().createStatement();
                // this result set is used to get all the users that are admin
                ResultSet rs = st.executeQuery("SELECT name, username, password, role, date_hired FROM accounts_tbl where accounts_id = " + Integer.parseInt(Global.accMenuClickedItems[0]));
                if(rs.next())
                {
                    //if there is already an admin, this automatically disables the admin radiobutton for creating new admin account
                    name_textField.setText(rs.getString(1));
                    username_textField.setText(rs.getString(2));
                    password_textField.setText(rs.getString(3));
                    if(rs.getString(4).equalsIgnoreCase("admin"))
                    {
                        admin_rb.setDisable(false);
                        admin_rb.selectedProperty().set(true);
                        employee_rb.selectedProperty().set(false);
                    }
                    else
                    {
                        admin_rb.setDisable(true);
                        employee_rb.selectedProperty().set(true);
                        admin_rb.selectedProperty().set(false);
                    }
                    String [] date = rs.getString(5).split("-");
                    switch(date[0])
                    {
                        case "Jan":
                            date[0] = "01";
                            break;
                        case "Feb":
                            date[0] = "02";
                            break;
                        case "Mar":
                            date[0] = "03";
                            break;
                        case "Apr":
                            date[0] = "04";
                            break;
                        case "May":
                            date[0] = "05";
                            break;
                        case "Jun":
                            date[0] = "06";
                            break;
                        case "Jul":
                            date[0] = "07";
                            break;
                        case "Aug":
                            date[0] = "08";
                            break;
                        case "Sep":
                            date[0] = "09";
                            break;
                        case "Oct":
                            date[0] = "10";
                            break;
                        case "Nov":
                            date[0] = "11";
                            break;
                        case "Dec":
                            date[0] = "12";
                            break;
                        
                    }
                    LocalDate hired = LocalDate.of(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]));
                    datePicker.setValue(hired);
                }
                Global.isForAddAccount = false;
            } catch (SQLException ex) {
                //this prints the error message if it encounters problem
                System.out.println(ex.getMessage());
            }
        }
        
        
    }    
}
