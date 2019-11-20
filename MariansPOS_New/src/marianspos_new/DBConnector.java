package marianspos_new;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnector
{
    Connection conn = null;
    
    public Connection getConnection()
    {
        //this function is for getting and opening the connection to the database
        try
        {
            //this class.forname is for getting the driver used for connecting the mysql or database to java
            Class.forName("com.mysql.jdbc.Driver");
            //this is to get the connection and opening the database marianspos
            conn = DriverManager.getConnection("jdbc:mysql://localhost/marianspos","root", "");
            System.out.println("Database is connected !");
        }
        catch(ClassNotFoundException | SQLException e)
        {
        }     
        //this returns or sets the connection
        return conn;
    }
}
