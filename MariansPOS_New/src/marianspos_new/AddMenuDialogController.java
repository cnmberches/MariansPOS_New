package marianspos_new;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class AddMenuDialogController implements Initializable {
    private final DBConnector con = new DBConnector();
    String category, available;
    boolean special = false;
    
    @FXML
    private ToggleButton available_tb; 
    
    @FXML
    private ComboBox category_cb;
    
    @FXML 
    private TextField name_tf, price_tf, persons_tf;
    
    @FXML
    private Button add_btn, deleteBtn;
    
    @FXML
    private TextArea description_ta;
    
    
    @FXML
    private void changeText(ActionEvent e)
    {
        if(available_tb.getText().equals("AVAILABLE"))
        {
            available_tb.setText("UNAVAILABLE");
        }
        else
        {
            available_tb.setText("AVAILABLE");
        }
    }
    
    @FXML
    private void add(ActionEvent e)
    {
        if(!name_tf.getText().isEmpty() && !price_tf.getText().isEmpty() && !category_cb.getEditor().getText().isEmpty() && !description_ta.getText().isEmpty())
        {
            //this will first create a string for the menu if it is available
            available = "Not Available";

            if(available_tb.isSelected())
            {
                //if the user clicked available, then store available
                available = "Available";
            }

            //this will get the text from the combo box of catefories
            category = category_cb.getEditor().getText();

            if(Global.isForAddMenu)
            {
                //if the user opens the menu for adding account do the following
                //ask or confirm the user to add the menu
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Add this menu?" , ButtonType.CANCEL, ButtonType.OK);
                alert.setTitle("Add Menu");
                //the show and wait functions waits the user to click between the buttons ok cancel
                alert.showAndWait();

                if(alert.getResult().equals(ButtonType.OK))
                {
                    //this will check if the catefory names has the category
                    if(Global.category_names.contains(category))
                    {
                        insert_row(name_tf.getText().replaceAll("//s", ""));
                    }
                    else
                    {
                        //if not, it will add the category to the database
                        Global.category_names.add(category);
                        try
                        {
                            //get the connection and prepare the database
                            PreparedStatement ps = con.getConnection().prepareStatement(
                                    "INSERT INTO Category_tbl (category_name) VALUES (?)");
                            //input the index of category
                            ps.setInt(1, Global.category_names.indexOf(category)+1);
                            ps.execute();
                            insert_row(name_tf.getText().replaceAll("//s", ""));
                        }
                        catch(SQLException ex)
                        {
                            ex.printStackTrace();
                        }
                    }

                    //this function is use to get the source file of the action event
                    final Node source = (Node) e.getSource();
                    //this gets the sctive stage or window of the file
                    final Stage stage = (Stage) source.getScene().getWindow();
                    //this is for closing the window
                    stage.close();
                }
            }
            else
            {
                //this is for the user if it will check if the user wanted to edit or update the account
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Update this menu?" , ButtonType.CANCEL, ButtonType.OK);
                alert.setTitle("Update Menu");
                //the show and wait functions waits the user to click between the buttons ok cancel
                alert.showAndWait();
                if(alert.getResult().equals(ButtonType.OK))
                {
                    try
                    {
                        //prepare the database
                        PreparedStatement ps = con.getConnection().prepareStatement(
                                "UPDATE MENUS_TBL SET category_id = ?, menus_name = ?, "
                            + "menus_price = ?, menus_description = ?, "
                            + "menu_status = ? where menus_id = ?");
                        ps.setInt(1, Global.category_names.indexOf(category)+1);
                        ps.setString(2, name_tf.getText());
                        ps.setString(3, price_tf.getText());
                        ps.setString(4, description_ta.getText());;
                        ps.setString(5, available);
                        ps.setInt(6, Integer.parseInt(Global.inventoryClickedItems[0]));
                        //execute or start aupdating the database
                        ps.execute();
                        con.getConnection().close();

                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Menu Updated", ButtonType.OK);
                        alert1.setTitle("Updated");
                        //the show and wait functions waits the user to click between the buttons ok cancel
                        alert1.showAndWait();
                        //this function is use to get the source file of the action event
                        final Node source = (Node) e.getSource();
                        //this gets the sctive stage or window of the file
                        final Stage stage = (Stage) source.getScene().getWindow();
                        //this is for closing the window
                        stage.close();
                    }
                    catch(NumberFormatException | SQLException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All information must be filled." , ButtonType.OK);
            alert.setTitle("Incomplete information.");
            //the show and wait functions waits the user to click between the buttons ok cancel
            alert.showAndWait();
        }
        
    }
    
        @FXML
    private void deleteMenu(ActionEvent e)
    {
        DBConnector con = new DBConnector();
        try
        {
            PreparedStatement ps = con.getConnection().prepareStatement("DELETE FROM menus_tbl WHERE menus_id = ?");
            ps.setInt(1, Integer.parseInt(Global.inventoryClickedItems[0]));
            ps.execute();
            con.getConnection().close();
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Menu Updated", ButtonType.OK);
            alert1.setTitle("Deleted");
            //the show and wait functions waits the user to click between the buttons ok cancel
            alert1.showAndWait();
            //this function is use to get the source file of the action event
            final Node source = (Node) e.getSource();
            //this gets the sctive stage or window of the file
            final Stage stage = (Stage) source.getScene().getWindow();
            //this is for closing the window
            stage.close();
        }
        catch (SQLException ex)
        {
            Logger.getLogger(MenuReportModuleController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void insert_row(String menuName)
    {
        //this is for the inserting the data to the menus database
        try
        {
            PreparedStatement checkPS = con.getConnection().prepareStatement(
                    "SELECT * FROM MENUS_TBL WHERE menus_name = ?");
            checkPS.setString(1, menuName);
            ResultSet rs = checkPS.executeQuery();
            
            if(!rs.next())
            {
                PreparedStatement ps = con.getConnection().prepareStatement(
                    "INSERT INTO MENUS_TBL (category_id, menus_name, "
                + "menus_price, menus_description, "
                + "menu_status) VALUES (?, ?, ?, ?,?)");
                ps.setInt(1, Global.category_names.indexOf(category)+1);
                ps.setString(2, name_tf.getText().replaceAll("//s", ""));
                ps.setInt(3, Integer.parseInt(price_tf.getText()));
                ps.setString(4, description_ta.getText());
                ps.setString(5, available);
                ps.execute();
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION, "Menu Added", ButtonType.OK);
                alert1.setTitle("Added");
                //the show and wait functions waits the user to click between the buttons ok cancel
                alert1.showAndWait();
                
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "This menu already exists!" ,ButtonType.OK);
                alert.setHeaderText("Existing Menu");
                alert.setTitle("");
                alert.showAndWait();
            }
            
            con.getConnection().close();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
        catch(NumberFormatException ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Price must be a numeric character" ,ButtonType.OK);
            alert.setHeaderText("Incorrect price format");
            alert.setTitle("");
            alert.showAndWait();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //at the start of the program, we will put or prepare the data of the table menus
        ObservableList<String> options = FXCollections.observableArrayList();
        //this function is for getting the categories and displaying it on the combo box
        for(int i = 0; i < Global.category_names.size(); i++)
        {
            options.add(Global.category_names.get(i));
        }
        category_cb.setItems(options);
        
        if(!Global.isForAddMenu)
        {
            //this will set the text to the items that were get from the accounts table
            category_cb.getEditor().setText(Global.inventoryClickedItems[1]);
            name_tf.setText(Global.inventoryClickedItems[2]);
            price_tf.setText(Global.inventoryClickedItems[3]);
            description_ta.setText(Global.inventoryClickedItems[4]);
            
            if(Global.inventoryClickedItems[5].equals("Available"))
            {
                //if the menu is available, set the available toggle button selected
                available_tb.fire();
            }
            //set the quantity of servings per menu
            //change the text of button to update
            add_btn.setText("Update");
        }
        else
        {
            deleteBtn.setVisible(false);
        }
    }    
}
