package marianspos_new;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MenuReportModuleController implements Initializable {
    private String[] columns = {"ID", "Category", "Menu Name", "Price", "Description", "Status"};

    private ObservableList<ObservableList> tbl_data;
    
    @FXML
    private Button add_btn, back_btn;
    
    @FXML
    private TableView menu_tbl;
        
    @FXML
    private void back(ActionEvent e) throws IOException
    {
        //this function is use to get the source file of the action event
        final Node source = (Node) e.getSource();
        //this gets the sctive stage or window of the file
        final Stage stage = (Stage) source.getScene().getWindow();
        //this is for closing the window
        stage.close();
    }
    
    @FXML
    public void clickItem(MouseEvent event) throws IOException
    {
        if (event.getClickCount() == 2 && menu_tbl.getSelectionModel().selectedIndexProperty().getValue() >= 0) //Checking double click
        {
            // if the user clicks two times, do the next functions
            //set the global varible to false because this will just display or edit the menu 
            Global.isForAddMenu = false;
            //this will get the clicked items and store it to the global array variable
            Global.inventoryClickedItems = menu_tbl.getSelectionModel().selectedItemProperty()
                    .get().toString().replace('[', ' ').replace(']', ' ').split(", ");
            for(int i = 0; i < Global.inventoryClickedItems.length ; i++)
            {
                //remove all the spaces from start and end
                Global.inventoryClickedItems[i] = Global.inventoryClickedItems[i].trim();
            }
            //this function is for opening a new window where its parameter include the fxml file in string, 
            //how the window will open (dialog or not),and its title 
            //fxml loader is used to get the fxml file wherein it has the codes for the design of the window
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddMenuDialog.fxml"));
            //this parent root is for loading all the codes for design
            Parent root1 = (Parent) fxmlLoader.load();
            //this stage is for creating the window
            Stage stage = new Stage();
            //this function is for how the window will open (window or dialog)
            stage.initModality(Modality.APPLICATION_MODAL);
            //this sets the title seen on the upper left of the window
            stage.setTitle("Edit");
            //this function makes the window not resizable
            stage.setResizable(false);
            //this makes sure that size is equal to the size of window based on the code
            stage.sizeToScene();
            //this puts the fxml file design in the window
            stage.setScene(new Scene(root1));  
            //this makes the window viewable to the user
            stage.show();
        }
    }
    
    @FXML
    private void add_menu(ActionEvent e) throws IOException
    {
        Global.isForAddMenu = true;
        //this function is for opening a new window where its parameter include the fxml file in string, 
        //how the window will open (dialog or not),and its title 
        //fxml loader is used to get the fxml file wherein it has the codes for the design of the window
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddMenuDialog.fxml"));
        //this parent root is for loading all the codes for design
        Parent root1 = (Parent) fxmlLoader.load();
        //this stage is for creating the window
        Stage stage = new Stage();
        //this function is for how the window will open (window or dialog)
        stage.initModality(Modality.APPLICATION_MODAL);
        //this sets the title seen on the upper left of the window
        stage.setTitle("Add a Menu");
        //this function makes the window not resizable
        stage.setResizable(false);
        //this makes sure that size is equal to the size of window based on the code
        stage.sizeToScene();
        //this puts the fxml file design in the window
        stage.setScene(new Scene(root1));  
        //this makes the window viewable to the user
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //connect to the databse
        DBConnector con = new DBConnector();
        try
        {
            //this function will add all the data and columns to the table
            String SQL1 = "SELECT category_name from category_tbl";
            ResultSet rs1 = con.getConnection().createStatement().executeQuery(SQL1);
            
            tbl_data = FXCollections.observableArrayList();
            String SQL2 = "SELECT * from menus_tbl";
            //ResultSet
            ResultSet rs2 = con.getConnection().createStatement().executeQuery(SQL2);
            
            for (int i = 0; i < rs2.getMetaData().getColumnCount(); i++) {
                final int j = i;
                //create a column
                TableColumn col = new TableColumn(columns[i]);
                switch(i)
                {
                    case 0:
                        col.setMaxWidth(70);
                        col.setMinWidth(70);
                        break;
                    case 1:
                        col.setMaxWidth(111);
                        col.setMinWidth(111);
                        break;
                    case 2:
                        col.setMaxWidth(157);
                        col.setMinWidth(157);
                        break;
                    case 3:
                        col.setMaxWidth(95);
                        col.setMinWidth(95);
                        break;
                    case 4:
                        col.setMaxWidth(289);
                        col.setMinWidth(289);
                        break;
                    case 5:
                        col.setMaxWidth(110);
                        col.setMinWidth(110);
                        break;
                    
                }
                col.setStyle(" -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>()
                {
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param)
                    {
                        // creat a row for header
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                
                //add the columns to the table
                menu_tbl.getColumns().addAll(col);
            }
            
            while(rs1.next())
            {
                //get the category names
                Global.category_names.add(rs1.getString(1));
            }
            
            while (rs2.next()) {
                //Iterate Row
                //create a observables list where the row is saved
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    //first is to check if the column in the databse is about categories
                    if(i == 2)
                    {
                        //get the category using the int or index saved in the databse
                        row.add(Global.category_names.get(rs2.getInt(i)));
                    }
                    else
                    {
                        //add the row
                        row.add(rs2.getString(i));
                    }
                }
                //add the row to the data
                tbl_data.add(row);
            }
            //set the items of table 
            menu_tbl.setItems(tbl_data);
        }
        catch(SQLException e)
        {
        }
    }    
}
