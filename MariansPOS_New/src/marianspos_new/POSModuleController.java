package marianspos_new;

import com.sun.javafx.print.Units;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class POSModuleController implements Initializable {
    private final String[] columns = {"Category", "Name", "Description", "Price"};
    private ObservableList<ObservableList> tbl_data;
    private ObservableList<ObservableList> orders_data;
    private int total = 0;
    private int orderNumber = 0;
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private ComboBox category_cb;
    
    @FXML
    private Button cancel_btn, add_btn;
    
    @FXML
    private TextField quantity_tf,search_tf;
    
    @FXML
    private Label name_lbl, description_lbl, price_lbl,cashierName_lbl, userID_lbl;
    
    @FXML
    private TableView menu_tbl;
    
    @FXML
    private TableView orders_tbl;
    
    static void printOperation(String text) {
        Text extractedText = new Text(text);
        extractedText.prefWidth(450);
        extractedText.setStyle("-fx-font-size: 8; -fx-font-family: monospace;");

        // use pane to place the text
        StackPane container = new StackPane(extractedText);
        container.setAlignment(Pos.TOP_LEFT);

        try
        {
            Constructor<Paper> c = Paper.class.getDeclaredConstructor(String.class,
                                         double.class, double.class, Units.class);
            c.setAccessible(true);
            Paper photo = c.newInstance("55x210", 55, 210, Units.MM);
            
            PrinterJob printerJob = PrinterJob.createPrinterJob();
            Printer printer = javafx.print.Printer.getDefaultPrinter();
            PageLayout pageLayout = printer.createPageLayout(photo,  PageOrientation.PORTRAIT, 0.0f, 0.0f, 8.0f, 8.0f);
            printerJob.getJobSettings().setPageLayout(pageLayout);
            if (printerJob != null) {

                if (printerJob.printPage(container)) {
                    printerJob.endJob();
                } else {
                    System.out.println("Failed to print");
                }
            } else {
                System.out.println("Canceled");
            }
        }
        catch(Exception e)
        {
            
        } 
    }
    
    @FXML
    public void clickItem(MouseEvent event) throws IOException
    {
        if(menu_tbl.getSelectionModel().selectedIndexProperty().intValue() != -1)
        {
            Global.isForAddMenu = false;
            Global.menuClickedItems = menu_tbl.getSelectionModel().selectedItemProperty()
                    .get().toString().replace('[', ' ').replace(']', ' ').split(", ");
            for(int i = 0; i < Global.menuClickedItems.length ; i++)
            {
                Global.menuClickedItems[i] = Global.menuClickedItems[i].trim();
            }

            name_lbl.setText(Global.menuClickedItems[1]);
            description_lbl.setText(Global.menuClickedItems[2]);
            price_lbl.setText(Global.menuClickedItems[3]);
        }
    }
    
    @FXML
    public void addOrders(ActionEvent e)
    {
        if(!orders_tbl.getItems().isEmpty())
        {
            orderNumber++;
            String orders = "";
            ObservableList<ObservableList> order1 = orders_tbl.getItems();
            for(ObservableList<ObservableList> order: order1)
            {
                String arrOrder[] = order.subList(0,4).toString().replace('[', ' ').replace(']', ' ').split(", ");
                orders = arrOrder[2] + " " + arrOrder[0] + "|";
                Global.orders.add(order);
            }
            
            Global.orderNumber = orderNumber;
            Global.totalCost = total;
            ObservableList<String> ordNum = FXCollections.observableArrayList();
            ordNum.add(String.valueOf(orderNumber));
            ordNum.add(orders);
            POSSecondModuleController.preparing_data.add(ordNum);
            
            printOperation("Order No: " + orderNumber + "/n" + orders + "\n\n\n\n-");
            orders_data.clear();
            total = 0;
        }
    }
    
    @FXML
    public void voidItem(MouseEvent event) throws IOException
    {
        if(!orders_data.isEmpty())
        {
            openModule("adminPassword.fxml", Modality.APPLICATION_MODAL, "Void").showAndWait();
            if(Global.isVoid)
            {
                ObservableList<ObservableList> selectedOrder, allOrders;
                allOrders = orders_tbl.getItems();
                selectedOrder = orders_tbl.getSelectionModel().getSelectedItems();
                total -= Integer.parseInt(selectedOrder.get(0).get(3).toString());
                
                selectedOrder.forEach(allOrders::remove);
                Global.isVoid = false;
            }
        }
    }

    @FXML
    private void add(ActionEvent e)
    {
        String quantity = quantity_tf.getText();
        ObservableList<String> row = FXCollections.observableArrayList();
        row.add(name_lbl.getText());
        row.add(price_lbl.getText());
        row.add(quantity);
        int cost = Integer.valueOf(quantity) * Integer.valueOf(price_lbl.getText());
        total += cost;
        
        row.add(String.valueOf(cost));
        if(cost > 0)
        {
            orders_tbl.getItems().add(row);
        }
        quantity_tf.setText("1");
        name_lbl.setText("Menu Name");
        price_lbl.setText("0");
        description_lbl.setText("Menu Description");
    }
    
    @FXML
    private void increase(ActionEvent e)
    {
        int quantity = Integer.valueOf(quantity_tf.getText()) + 1;
        quantity_tf.setText(String.valueOf(quantity));
    }
    
    @FXML
    private void logOut(ActionEvent event) throws IOException
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to log out?" , ButtonType.NO, ButtonType.YES);
        alert.setTitle("Log out user?");
        //the show and wait functions waits the user to click between the buttons ok cancel
        alert.showAndWait();
        if(alert.getResult().equals(ButtonType.YES))
        {
            openModule("SignInModule.fxml", Modality.WINDOW_MODAL, "Marian's Point of Sales System").show();
            Node node = (Node) event.getSource();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.close();
        }
    }
    
    @FXML
    private void decrease(ActionEvent e)
    {
        int quantity = Integer.valueOf(quantity_tf.getText());
        if(quantity <= 1)
        {
            quantity_tf.setText(String.valueOf(quantity));
        }
        else
        {
            quantity--;
            quantity_tf.setText(String.valueOf(quantity));
        }
    }
    
    @FXML
    private void loadMenu(ActionEvent e)
    {   
        if(category_cb.getValue().toString().equals("All Menu"))
        {
            loadMenu("*");
        }
        else
        {
            loadMenu(category_cb.getValue().toString());
        }
    }
    
    @FXML
    private void search(KeyEvent e)
    {
        searchMenu(search_tf.getText());  
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    { 
        cashierName_lbl.setText(Global.name);
        userID_lbl.setText(Global.account_id);
        loadMenu("*");
        String[] column_names = {"Name", "Price", "Quantity", "Cost"};
        for (int i = 0; i < column_names.length; i++) {
            final int j = i;
            TableColumn col = new TableColumn(column_names[i]);
            col.setStyle(" -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>()
            {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param)
                {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });

            orders_tbl.getColumns().addAll(col);
        }
        orders_tbl.setItems(orders_data);
        
        //category_cb.
        DBConnector con = new DBConnector();
        try
        {
            //this function will add all the data and columns to the table
            String SQL1 = "SELECT category_name from category_tbl";
            ResultSet rs1 = con.getConnection().createStatement().executeQuery(SQL1);
            
            ObservableList<String> options = FXCollections.observableArrayList();
            options.add("All Menu");
            
            while(rs1.next())
            {
                //get the category names
                options.add(rs1.getString(1));
            }
            con.getConnection().close();
            
            category_cb.setItems(options);
            total = 0;
        }
        catch(Exception e)
        {
            
        }
    }
    
    private void loadMenu(String categoryName)
    {
        DBConnector con = new DBConnector();
        try
        {
            tbl_data = FXCollections.observableArrayList();
            tbl_data.clear();
            menu_tbl.getColumns().clear();
            menu_tbl.getItems().clear();
            orders_data = FXCollections.observableArrayList();
            
            String SQL2;
            if(!categoryName.equalsIgnoreCase("*"))
            {
                SQL2 = "select category_name, menus_name, menus_description, menus_price"
                + " from menus_tbl A inner join category_tbl S on A.category_id = S.category_id where"
                + " category_name = '" + categoryName + "' AND menu_status = 'Available';";
            }
            else
            {
                SQL2 = "select category_name, menus_name, menus_description, menus_price"
                + " from menus_tbl A inner join category_tbl S on A.category_id = S.category_id WHERE menu_status = 'Available'";
            }
             
            //ResultSet
            ResultSet rs2 = con.getConnection().createStatement().executeQuery(SQL2);
            for (int i = 0; i < rs2.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(columns[i]);
                col.setStyle(" -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>()
                {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param)
                    {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                menu_tbl.getColumns().addAll(col);
            }

            while (rs2.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs2.getString(i));
                }
                tbl_data.add(row);
            }
            menu_tbl.setItems(tbl_data);
            con.getConnection().close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();    
        }
    }
    
     private void searchMenu(String wordToSearch)
    {
        DBConnector con = new DBConnector();
        try
        {
            String toSearch = wordToSearch;
            String SQL2 = "select category_name, menus_name, menus_description, menus_price "
                + "from menus_tbl A inner join category_tbl S on A.category_id = S.category_id "
                    + "WHERE menus_name LIKE '%" + toSearch + "%' AND menu_status = 'Available'";
            if(toSearch.isEmpty())
            {
                SQL2 = "select category_name, menus_name, menus_description, menus_price "
                + "from menus_tbl A inner join category_tbl S on A.category_id = S.category_id "
                    + "WHERE menu_status = 'Available'";
            }
            tbl_data = FXCollections.observableArrayList();
            tbl_data.clear();
            menu_tbl.getColumns().clear();
            menu_tbl.getItems().clear();
            orders_data = FXCollections.observableArrayList();
             
            //ResultSet
            ResultSet rs2 = con.getConnection().createStatement().executeQuery(SQL2);
            for (int i = 0; i < rs2.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(columns[i]);
                col.setStyle(" -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>()
                {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param)
                    {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                menu_tbl.getColumns().addAll(col);
            }

            while (rs2.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs2.getString(i));
                }
                tbl_data.add(row);
            }
            menu_tbl.setItems(tbl_data);
            con.getConnection().close();
        }
        catch(SQLException e)
        {
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
        Scene scene = new Scene(root);
        if(fxmlFile.equals("SignInModule.fxml"))
        {
            scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_ANY);
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    try {
                        Global.isForAdminModule = true;
                        Stage x = openModule("adminPassword.fxml", Modality.APPLICATION_MODAL, "Enter password");
                        x.show();
                        ke.consume(); // <-- stops passing the event to next node
                    } catch (IOException ex) {
                        Logger.getLogger(MariansPOS_New.class.getName()).log(Level.SEVERE, null, ex);
                        ex.printStackTrace();
                    }
                    System.out.println("dd");
                }
            }
        });
        }
        stage.setScene(scene);  
        //this makes the window viewable to the user
        

        //this if statement is to check if the window is showned not as a dialog
        //if it is WINDOW_MODAL, the main menu or log in module will close from the screen
        return stage;
    }
}
