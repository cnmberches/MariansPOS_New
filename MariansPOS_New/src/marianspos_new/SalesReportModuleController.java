package marianspos_new;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class SalesReportModuleController implements Initializable {
    private String[] columns = {"ID", "Accounts ID", "Orders", "Grand Total", "Date Ordered"};
    private double sales = 0;
    private ObservableList<ObservableList> tbl_data;
    
    @FXML
    private DatePicker fromDatePicker, toDatePicker;
    @FXML
    private Label salesTotal_lbl;
    @FXML
    private TableView transaction_tbl;
    @FXML
    private ComboBox shift_cb;
    
    @FXML
    private void accLogs(ActionEvent event) throws IOException {
        Stage open = openModule("AccountLogsModule.fxml", Modality.WINDOW_MODAL, "Account Logs");
        open.setOnCloseRequest(new EventHandler<WindowEvent>()
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
                            Stage a = openModule("MainMenuModule.fxml", Modality.WINDOW_MODAL, "Main Menu");
                            a.setOnCloseRequest(new EventHandler<WindowEvent>()
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
                                                openModule("SignInModule.fxml", Modality.WINDOW_MODAL, "Main Menu").show();
                                            }
                                            catch (IOException ex)
                                            {
                                            }
                                        }
                                    });
                                }
                            });
                            a.show();
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                });
            }
        });
        open.show();
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DBConnector con = new DBConnector();
        try
        {
            tbl_data = FXCollections.observableArrayList();
            String SQL2 = "SELECT transactions_id, name, orders, grand_total, date_ordered from transactions_tbl A inner join accounts_tbl B on B.accounts_id = A.accounts_id";
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
                transaction_tbl.getColumns().addAll(col);
            }
            
            while (rs2.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    String data = rs2.getString(i);
                    row.add(data);
                    if(i == 4)
                    {
                        sales += Double.parseDouble(data);
                    }
                }
                tbl_data.add(row);
            }
            transaction_tbl.setItems(tbl_data);
            transaction_tbl.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY);
            
            salesTotal_lbl.setText(String.valueOf(sales));
            
            ObservableList<String> options = FXCollections.observableArrayList("AM", "PM", "ALL");
            shift_cb.setItems(options);
        }
        catch(SQLException e)
        {
            
        }
    }    
    
    @FXML
    private void menuReport(ActionEvent event) throws IOException {
        Stage open = openModule("MenuReportModule.fxml", Modality.WINDOW_MODAL, "Food Management");open.setOnCloseRequest(new EventHandler<WindowEvent>()
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
                            Stage a = openModule("MainMenuModule.fxml", Modality.WINDOW_MODAL, "Main Menu");
                            a.setOnCloseRequest(new EventHandler<WindowEvent>()
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
                                                openModule("SignInModule.fxml", Modality.WINDOW_MODAL, "Main Menu").show();
                                            }
                                            catch (IOException ex)
                                            {
                                            }
                                        }
                                    });
                                }
                            });
                            a.show();
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                });
            }
        });
        open.show();
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void accountReport(ActionEvent event) throws IOException {
        Stage open = openModule("AccountManagementModule.fxml", Modality.WINDOW_MODAL, "Account Manager");
        open.setOnCloseRequest(new EventHandler<WindowEvent>()
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
                            Stage a = openModule("MainMenuModule.fxml", Modality.WINDOW_MODAL, "Main Menu");
                            a.setOnCloseRequest(new EventHandler<WindowEvent>()
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
                                                openModule("SignInModule.fxml", Modality.WINDOW_MODAL, "Main Menu").show();
                                            }
                                            catch (IOException ex)
                                            {
                                            }
                                        }
                                    });
                                }
                            });
                            a.show();
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                });
            }
        });
        open.show();
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
    
    @FXML 
    private void export(ActionEvent e)
    {
        DBConnector con = new DBConnector();
        try
        {
            String SQL2 = "SELECT * from transactions_tbl";
            ResultSet rs2 = con.getConnection().createStatement().executeQuery(SQL2);
            CSVWriter writer = new CSVWriter(new FileWriter("SalesReport.csv"), '\t');
            Boolean includeHeaders = true; 

            writer.writeAll(rs2, includeHeaders);

            writer.close();
        }
        catch(Exception ed)
        {
            
        }
    }
    
    @FXML 
    private void load(ActionEvent e)
    {
        if(!fromDatePicker.getEditor().getText().isEmpty() && !toDatePicker.getEditor().getText().isEmpty() && !shift_cb.getSelectionModel().isSelected(-1))
        {
            sales = 0;
            String [] fromDateArr = fromDatePicker.getEditor().getText().split("/");
            String [] toDateArr = toDatePicker.getEditor().getText().split("/");
            String fromDate = fromDateArr[2] + "-" + fromDateArr[0] + "-" + fromDateArr[1];
            String toDate = toDateArr[2] + "-" + toDateArr[0] + "-" + toDateArr[1];

            DBConnector con = new DBConnector();
            try
            {
                tbl_data = FXCollections.observableArrayList();
                transaction_tbl.getColumns().clear();
                String SQL2;

                if(shift_cb.getValue().toString().equalsIgnoreCase("am"))
                {
                    SQL2 = "SELECT transactions_id, name, orders, grand_total, date_ordered from transactions_tbl A inner join accounts_tbl B on B.accounts_id = A.accounts_id where date_ordered BETWEEN '"+ fromDate + " 6:00:00' AND '" + toDate +" 17:59:59'";
                }
                else if(shift_cb.getValue().toString().equalsIgnoreCase("pm"))
                {
                   SQL2 = "SELECT transactions_id, name, orders, grand_total, date_ordered from transactions_tbl A inner join accounts_tbl B on B.accounts_id = A.accounts_id where date_ordered BETWEEN '"+ fromDate + " 18:00:00' AND '" + toDate +" 05:59:59'"; 
                }
                else
                {
                   SQL2 ="SELECT transactions_id, name, orders, grand_total, date_ordered from transactions_tbl A inner join accounts_tbl B on B.accounts_id = A.accounts_id where date_ordered BETWEEN '"+ fromDate + "' AND '" + toDate +"'";
                }

                System.out.println(SQL2);
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
                    transaction_tbl.getColumns().addAll(col);
                }

                while (rs2.next()) {
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= rs2.getMetaData().getColumnCount(); i++) {
                        //Iterate Column
                        String data = rs2.getString(i);
                        row.add(data);
                        if(i == 4)
                        {
                            sales += Double.parseDouble(data);
                        }
                    }
                    tbl_data.add(row);
                }
                transaction_tbl.setItems(tbl_data);
                transaction_tbl.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY);

                salesTotal_lbl.setText(String.valueOf(sales));
            }
            catch(SQLException ex)
            {
                ex.printStackTrace();
            }
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
