package library;

public class Library {

    public static void main(String[] args) {
        if(new DBConnect().openConnection() != null) new Login().setVisible(true);
    }   
}