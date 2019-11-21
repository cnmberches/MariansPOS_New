package marianspos_new;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DBConnector con = new DBConnector();
        try
        {
            tbl_data = FXCollections.observableArrayList();
            String SQL2 = "SELECT * from transactions_tbl";
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
        catch(SQLException e)
        {
            
        }
    }    
    
    @FXML 
    private void load(ActionEvent e)
    {
        if(!fromDatePicker.getEditor().getText().isEmpty() && !toDatePicker.getEditor().getText().isEmpty())
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
                String SQL2 = "SELECT * from transactions_tbl where date_ordered BETWEEN '"+ fromDate + "' AND '" + toDate +"'";
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
}
