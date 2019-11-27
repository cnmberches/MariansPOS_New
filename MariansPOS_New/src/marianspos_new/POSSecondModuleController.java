package marianspos_new;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class POSSecondModuleController implements Initializable {
    private ObservableList<ObservableList> serving_data = FXCollections.observableArrayList();
    public static ObservableList<ObservableList> preparing_data = FXCollections.observableArrayList();
    int clickCounter = 0;
    int clickCounter1 = 0;
    
    @FXML
    private TableView preparing_tbl, serving_tbl;
    
    @FXML
    public void serveAdd(MouseEvent event) throws IOException
    {
        if(preparing_tbl.getSelectionModel().selectedIndexProperty().intValue() != -1)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Serve this?" , ButtonType.CANCEL, ButtonType.OK);
            alert.setTitle("");
            //the show and wait functions waits the user to click between the buttons ok cancel
            alert.showAndWait();

            if(alert.getResult() == ButtonType.OK)
            {
                String itemClick [] = preparing_tbl.getItems().subList(0, 1).toString().replace('[', ' ').replace(']', ' ').split(", ");
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(itemClick[0].trim());
                row.add(itemClick[1].trim());
                serving_tbl.getItems().add(row);

                ObservableList<ObservableList> selectedOrder, allOrders;
                allOrders = preparing_tbl.getItems();
                selectedOrder = preparing_tbl.getSelectionModel().getSelectedItems();

                selectedOrder.forEach(allOrders::remove);
            }
        }
    }
    
    @FXML
    public void update(MouseEvent event) throws IOException
    {
        if(serving_tbl.getSelectionModel().selectedIndexProperty().intValue() != -1)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Remove this?" , ButtonType.CANCEL, ButtonType.OK);
            alert.setTitle("");
            //the show and wait functions waits the user to click between the buttons ok cancel
            alert.showAndWait();

            if(alert.getResult() == ButtonType.OK)
            {
                openModule("MenuDialog.fxml", Modality.APPLICATION_MODAL, "Confirm").showAndWait();
                ObservableList<ObservableList> selectedOrder, allOrders;
                allOrders = serving_tbl.getItems();
                selectedOrder = serving_tbl.getSelectionModel().getSelectedItems();

                selectedOrder.forEach(allOrders::remove);
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String columns[] = {"Order No.", "Orders"};
        for (int i = 0; i < columns.length; i++)
        {
            final int j = i;
            TableColumn col = new TableColumn(columns[i]);
            if(i==0)
            {
                col.setMinWidth(100);
                col.setMaxWidth(100);
            }
            col.setStyle(" -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>()
            {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param)
                {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            preparing_tbl.getColumns().addAll(col);
        }
        
        for (int i = 0; i < columns.length; i++)
        {
            final int j = i;
            TableColumn col = new TableColumn(columns[i]);
            if(i==0)
            {
                col.setMinWidth(100);
                col.setMaxWidth(100);
            }
            col.setStyle(" -fx-font-family: 'Poppins'; -fx-font-size: 14px;");
            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>()
            {
                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param)
                {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            serving_tbl.getColumns().addAll(col);
        }
 
        //serving_tbl.getColumns().addAll(orderNum, orders);
        //serving_tbl.setItems(serving_data);
        preparing_tbl.setItems(preparing_data);
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
