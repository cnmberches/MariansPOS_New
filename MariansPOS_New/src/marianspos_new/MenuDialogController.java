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
import javafx.beans.value.ChangeListener;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class MenuDialogController implements Initializable {   
    private int total;
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private Button cancel_btn, add_btn;
    
    @FXML
    private ToggleButton discount_tb; 
    
    @FXML
    private ToggleGroup status_tg;
    
    @FXML
    private RadioButton senior_rb, pwd_rb, dineIn_rb, takeOut_rb;
    
    @FXML
    private Label discount_lbl, tax_lbl, total_lbl, grandTotal_lbl, change_lbl;
    
    @FXML
    private TextField amountTendered_tf;
    
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
    
    @FXML
    private void radioButtons(ActionEvent e)
    {
        //cheks if the discount button is selected
        if(pwd_rb.isSelected() || senior_rb.isSelected())
        {
            float discount = total * .20f;
            //this willl set the value of discount
            discount_lbl.setText(String.valueOf(discount));
            //automatically minus the discount to total
            grandTotal_lbl.setText(String.valueOf(total-discount));
        }
        else
        {
            //set the discount to 0 and grand total to the original value
            discount_lbl.setText("00.00");
            grandTotal_lbl.setText(String.valueOf(total));
        }
    }
    
    @FXML
    private void order(ActionEvent e) throws IOException
    {
        if(!amountTendered_tf.getText().isEmpty() && Integer.parseInt(amountTendered_tf.getText()) != 0 && Double.parseDouble(change_lbl.getText()) >= 0)
        {
            Calendar now = Calendar.getInstance();
            DBConnector db = new DBConnector();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
            String status = "";
            if(status_tg.getSelectedToggle().equals(dineIn_rb))
            {
                status = "Dine In";
            }
            else
            {
                status = "Take Out";
            }
            try
            {
                TextArea receiptLayout = new TextArea();
                receiptLayout.setPrefWidth(450);
                receiptLayout.setStyle("-fx-font-size: 11; -fx-font-family: monospace;");
                receiptLayout.appendText("Marian's Pares, Bulalo, atbp.\n" +
			"A Mabini St. Brgy Guinhawa,\nCity of Malolos Bulacan.\n" + 
			"Landline: (044) 305 0237 \nGlobe: 0956 277 8137\n" +
			date + "\n\n");
                
                //first is to get a connection and create a statement
                Statement st = db.getConnection().createStatement();
                //this query is for inserting the values name, username, password, role, and date_hired
                //it uses "?" in the values for preparedstatement
                String sql = "INSERT INTO transactions_tbl(accounts_id, orders, date_ordered, grand_total) "
                            + "VALUES(?, ?, ?, ?)";
                String orders = "";
                String receiptOrders = "";
                ObservableList<ObservableList> order1 = Global.orders;
                for(ObservableList<ObservableList> order: order1)
                {
                    String arrOrder[] = order.subList(0,4).toString().replace('[', ' ').replace(']', ' ').split(", ");
                    orders = order.subList(0,4).toString().replace('[', ' ').replace(']', ' ').replaceFirst(",", "-").replaceFirst(", ", "*").replaceFirst(", ", "=")
                            + status + " | ";
                    receiptOrders += String.format("%-30s\n", arrOrder[0].trim());
                    receiptOrders += String.format("%-5s * %-10s Total:%-6s\n\n", arrOrder[1].trim(), arrOrder[2].trim(), arrOrder[3].trim());
                }
                
                //first is to get the connection then prepare the statement query
                //name.price,quantity,cost
                PreparedStatement ps = db.getConnection().prepareStatement(sql);
                //this inserts the data by index and its corresponding value
                
                ps.setString(1, Global.account_id);
                ps.setString(2, orders);
                ps.setString(3, date);
                ps.setString(4, grandTotal_lbl.getText());
                //this function is for commanding the system to do the query which inserts a new row/data in database
                ps.executeUpdate();

                ResultSet rs2 = db.getConnection().createStatement().executeQuery("SELECT transactions_id FROM transactions_tbl");

                int id = 0;
                while(rs2.next())
                {
                    id = rs2.getInt("transactions_id");
                }
                
                db.getConnection().close();
                
                receiptLayout.appendText(status + "\n");
                receiptLayout.appendText("Transaction ID: " +  id + "\n");
                receiptLayout.appendText("Order Number: " +  Global.orderNumber + "\n");
                receiptLayout.appendText("========================================================\n");
                receiptLayout.appendText("\n");
                receiptLayout.appendText(receiptOrders);
                receiptLayout.appendText(String.format("%-11s:%-6s\n","Tax" ,tax_lbl.getText()));
                receiptLayout.appendText(String.format("%-11s:%-6s\n", "Discount" ,discount_lbl.getText()));
                receiptLayout.appendText(String.format("%-11s:%-6s\n", "Grand Total" ,grandTotal_lbl.getText()));
                receiptLayout.appendText(String.format("%-11s:%-6s\n","Cash" ,amountTendered_tf.getText()));
                receiptLayout.appendText(String.format("%-11s:%-6s\n", "Change" ,change_lbl.getText()));
                receiptLayout.appendText("Remarks: ________________________\n\n\n\n");
                receiptLayout.appendText("-");
                
            } catch (SQLException ex) {
                //this prints the error message if it encounters problem
                ex.printStackTrace();
            }
            cancel_btn.fire();
        }
    }
    
    static void printOperation(TextArea textDocument) {
        Text extractedText = new Text(textDocument.textProperty().get());
        extractedText.prefWidth(450);
        extractedText.setStyle("-fx-font-size: 6; -fx-font-family: monospace;");

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
    private void discount_toggle(ActionEvent e)
    {
        if(discount_tb.isSelected())
        {
            senior_rb.setDisable(false);
            pwd_rb.setDisable(false);
        }
        else
        {
            senior_rb.setDisable(true);
            pwd_rb.setDisable(true);
            pwd_rb.setSelected(false);
            senior_rb.setSelected(false);
            discount_lbl.setText("0");
            grandTotal_lbl.setText(String.valueOf(total));
        }
    }
    
    @FXML
    private void amountKeyType(KeyEvent e)
    {
        if(!amountTendered_tf.getText().isEmpty())
        {
            double amountTendered = Double.valueOf(amountTendered_tf.getText());
            double change = Math.round((amountTendered - Double.valueOf(grandTotal_lbl.getText()))* 100) / 100.0f;
            change_lbl.setText(String.valueOf(change));
        }    
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        total = Global.totalCost;
        float tax = Math.round((total  * .12)* 100) / 100.0f;
        
        total_lbl.setText(String.valueOf(total));
        grandTotal_lbl.setText(String.valueOf(total));
        tax_lbl.setText(String.valueOf(tax));
        amountTendered_tf.setText("0");
        
        amountTendered_tf.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                if(!newValue.matches("\\d*")) {
                    amountTendered_tf.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
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
