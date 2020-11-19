package marianspos_new;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Global
{
    
    static String name, role, account_id, username;
    static String[] inventoryClickedItems;
    static String[] menuClickedItems;
    static String[] accMenuClickedItems;
    static String mode;
    static ArrayList<String> category_names = new ArrayList<>();
    static ArrayList<Integer> category_id = new ArrayList<>();
    static boolean isForAddMenu = true;
    static boolean isForAddAccount = true;
    static ArrayList <Integer> totalCost = new ArrayList();
    static boolean isVoid = false;
    static boolean isForAdminModule = false;
    static boolean isPasswordCorrect = false;
    static ObservableList<ObservableList> orders = FXCollections.observableArrayList();
    static int transaction_id;
    static ArrayList<Integer> orderNumber = new ArrayList();
    static boolean isConfirmed = false;
    static int orderNumberIndex = 0;
}

