package library;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DBConnect {
    private Connection conn;
    
    String url = "jdbc:mysql://localhost/";
    String dbname = "Library";
    String driver = "com.mysql.jdbc.Driver";
    String user = "root";
    String pass = "";
     
    public Connection openConnection(){
        if(conn == null){          
            try{
                Class.forName(driver);
                this.conn = (Connection) DriverManager.getConnection(url + dbname, user, pass);
            }
            catch (ClassNotFoundException | SQLException sqle){
                JOptionPane.showMessageDialog(null, sqle, "Engggkkkkk!",2);
            }
        }
        return conn;
    }
    
    boolean login(String username, String password){      
        boolean check = false;
        String query = "SELECT * FROM `login` WHERE `user`=? AND `pass`=?;";
        PreparedStatement ps;
        ResultSet rs;
        
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();

            if(rs.next()){
                check = true;
            }
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return check;
    }
    
    DefaultTableModel getReturned(String query){
        DefaultTableModel dt = new DefaultTableModel(){
            public boolean isCellEditable(int row, int colunmn){
                return false;
            }
        };
        
        String cols[] = {"DATE BORROWED", "STUDENT ID","STUDENT NAME","ISBN","TITLE", "DATE RETURNED"};
        for(String col : cols){
            dt.addColumn(col);
        }
        
        PreparedStatement ps;
        ResultSet rs;
        
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            rs = ps.executeQuery();
            
            while(rs.next()){
                dt.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                });
            }    
        }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return dt;
    }
    
    DefaultTableModel getBorrowed(String query){
        DefaultTableModel dt = new DefaultTableModel(){
            public boolean isCellEditable(int row, int colunmn){
                return false;
            }
        };
        
        String cols[] = {"DATE BORROWED", "STUDENT ID","STUDENT NAME","ISBN","TITLE"};
        for(String col : cols){
            dt.addColumn(col);
        }
        
        PreparedStatement ps;
        ResultSet rs;
        
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            rs = ps.executeQuery();
            
            while(rs.next()){
                dt.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    
                });
            }    
        }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return dt;
    }
    
    DefaultTableModel getBooks(String query){
        DefaultTableModel dt = new DefaultTableModel(){
            public boolean isCellEditable(int row, int colunmn){
                return false;
            }
        };
        
        String cols[] = {"ISBN", "TITLE","CATEGORY","AUTHOR","PUBLISHER","PUBLISH YEAR","COPIES"};
        for(String col : cols){
            dt.addColumn(col);
        }
        
        PreparedStatement ps;
        ResultSet rs;
        
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            rs = ps.executeQuery();
            
            while(rs.next()){
                dt.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2).substring(0, 1).toUpperCase() + rs.getString(2).substring(1).toLowerCase(),
                    getCat(rs.getString(3)),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6).substring(0, 4),
                    rs.getString(7),    
                });
            }    
        }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return dt;
    }
    
    DefaultTableModel getStudents(String query){
        DefaultTableModel dt = new DefaultTableModel(){
            public boolean isCellEditable(int row, int colunmn){
                return false;
            }
        };
        
        String cols[] = {"STUDENT #", "FIRST NAME","LAST NAME","COURSE","GENDER","EMAIL","PHONE #"};
        for(String col : cols){
            dt.addColumn(col);
        }
        
        PreparedStatement ps;
        ResultSet rs;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            rs = ps.executeQuery();
            
            while(rs.next()){
                dt.addRow(new Object[]{
                    rs.getString(1),
                    rs.getString(2).substring(0, 1).toUpperCase() + rs.getString(2).substring(1).toLowerCase(),
                    rs.getString(3).substring(0, 1).toUpperCase() + rs.getString(3).substring(1).toLowerCase(),
                    getCourse(rs.getString(4)),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7),    
                });
            }      
        }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return dt;
    }
    
    String getCat(String id){
        String cat = "1";
        String query = "SELECT * FROM `category_details` WHERE category_id = '"+ id +"'";
       
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) cat = rs.getString(2);
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cat;
    }
    
    int getCatID(String cat){
        int id = 1;
        String query = "SELECT * FROM `category_details` WHERE category_name = '"+ cat +"'";
       
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) id = rs.getInt(1);
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
       return id;
    }
    
    String getCourse(String id){
        String cat = "1";
        String query = "SELECT * FROM `course_details` WHERE course_id = '"+ id +"'";
       
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) cat = rs.getString(2);
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cat;
    }
    
    int getCourseID(String course){
        int id = 1;
        String query = "SELECT * FROM `course_details` WHERE course_desc = '"+ course +"'";
       
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) id = rs.getInt(1);
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
       return id;
    }
    
    int getCopy(String isbn){
        int copy = 0;
        String query = "SELECT copies FROM `book_details` WHERE `book_details`.`ISBN` = '"+ isbn +"';";
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) copy = rs.getInt(1);
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return copy;
    }
    
    String getBookCount(){
        String count = "0";
//        String query = "SELECT COUNT(*) FROM `book_details`;";
        String query = "SELECT SUM(copies) FROM `book_details`;";
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) count = String.valueOf(rs.getInt(1));
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    String getStudentCount(){
        String count = "0";
        String query = "SELECT COUNT(*) FROM `student_details`;";
       
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) count = String.valueOf(rs.getInt(1));  
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    String getBorrowCount(){
        String count = "0";
        String query = "SELECT COUNT(*) FROM `borrow_details` WHERE date_borrowed = CURRENT_DATE();";
        
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) count = String.valueOf(rs.getInt(1));  
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    String getReturnCount(){
        String count = "0";
        String query = "SELECT COUNT(*) FROM `return_history` WHERE date_return = CURRENT_DATE();";
        
        ResultSet rs;
        Statement st;
        try{
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) count = String.valueOf(rs.getInt(1));  
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }
    
    void decreaseCopy(String isbn){
        String query = "UPDATE `book_details` SET `copies` = ? WHERE `book_details`.`ISBN` = ?;";
        PreparedStatement ps;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
                     
            ps.setInt(1, getCopy(isbn) - 1);
            ps.setString(2, isbn);
            ps.executeUpdate();
         
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    void increaseCopy(String isbn){
        String query = "UPDATE `book_details` SET `copies` = ? WHERE `book_details`.`ISBN` = ?;";
        PreparedStatement ps;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
                     
            ps.setInt(1, getCopy(isbn) + 1);
            ps.setString(2, isbn);
            ps.executeUpdate();
         
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    boolean insertBorrow(String studentid, String isbn){ 
        if(getCopy(isbn) > 0){
            String query = "INSERT INTO `borrow_details`"
                    + "(`id`, `student_id`, `book_id`, `date_borrowed`)"
                    + "VALUES (NULL, ?, ?, ?)";
            PreparedStatement ps;
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            try{
                ps = (PreparedStatement) openConnection().prepareStatement(query);
                ps.setString(1, studentid);
                ps.setString(2, isbn);
                ps.setString(3, df.format(new Date()));

                ps.executeUpdate();
                decreaseCopy(isbn);
                return true;
            }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }   
        }
        JOptionPane.showMessageDialog(null, "All books are borrowed !", "Warning",2);
        return false;
    }
    
    void logReturn(String studentid, String isbn){ 
        
            String query = "INSERT INTO `return_history` (`date_borrow`, `sid`, `bid`, `date_return`)"
                    +"SELECT `date_borrowed`, `student_id`, `book_id`, CURRENT_DATE()"
                    +"FROM `borrow_details`"
                    +"WHERE `student_id`=? && `book_id`=?";
            PreparedStatement ps;
           
            try{
                ps = (PreparedStatement) openConnection().prepareStatement(query);
                ps.setString(1, studentid);
                ps.setString(2, isbn);
              
                ps.executeUpdate();

            }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);

            }   
       
    }
    
    boolean returnBook(String sid, String isbn){      
        boolean ret = false;
        String query = "SELECT * FROM `borrow_details` WHERE `student_id` = ? AND `book_id` = ?;";
        PreparedStatement ps;
        ResultSet rs;
        
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, sid);
            ps.setString(2, isbn);

            rs = ps.executeQuery();
            
            if(rs.next()){
                
                
                increaseCopy(isbn);
                logReturn(sid, isbn);
                String query2 = "DELETE FROM `borrow_details` WHERE `student_id` = ? AND `book_id` = ?;";
                PreparedStatement ps2;

                try{
                    ps2 = (PreparedStatement) openConnection().prepareStatement(query2);
                    ps2.setString(1, sid);
                    ps2.setString(2, isbn);
                    ps2.executeUpdate();     
                    
                    ret = true;
                }catch(SQLException ex){
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }
            
        }catch(SQLException ex){
            System.err.println(ex);
        }
        return ret;
    }
    
    boolean insertStudent(Student student){ 
        String query = "INSERT INTO `student_details`"
                + "(`student_id`, `first_name`, `last_name`, `course_id`, `gender`, `email`, `phone_number`)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, student.getStudentID());
            ps.setString(2, student.getFname().substring(0, 1).toUpperCase() + student.getFname().substring(1).toLowerCase());
            ps.setString(3, student.getLname().substring(0, 1).toUpperCase() + student.getLname().substring(1).toLowerCase());
            ps.setInt(4, getCourseID(student.getCourse()));
            ps.setString(5, student.getGender());
            ps.setString(6, student.getEmail());
            ps.setString(7, student.getNumber());
            ps.executeUpdate();
            
            return true;
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }   
    }
      
    boolean updateStudent(Student student){
        String query = "UPDATE `student_details`"
                    + "SET `first_name` = ?, `last_name` = ?, `course_id` = ?, `gender` = ?, `email` = ?, `phone_number` = ?"
                    + "WHERE `student_details`.`student_id` = ?";
        PreparedStatement ps;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
                     
            ps.setString(1, student.getFname().substring(0, 1).toUpperCase() + student.getFname().substring(1).toLowerCase());
            ps.setString(2, student.getLname().substring(0, 1).toUpperCase() + student.getLname().substring(1).toLowerCase());
            ps.setInt(3, getCourseID(student.getCourse()));
            ps.setString(4, student.getGender());
            ps.setString(5, student.getEmail());
            ps.setString(6, student.getNumber());
            ps.setString(7, student.getStudentID());
            ps.executeUpdate();
            
            return true;
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }   
    }
    
    void deleteStudent(String studentid){     
        String query = "DELETE FROM `student_details` WHERE `student_details`.`student_id` = ?";
        PreparedStatement ps;
        
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, studentid);
            ps.executeUpdate();        
        }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    boolean insertBook(Book book){ 
        String query = "INSERT INTO `book_details`"
                + "(`ISBN`, `title`, `category_id`, `author`, `publisher`, `publish_year`, `copies`)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle().substring(0, 1).toUpperCase() + book.getTitle().substring(1).toLowerCase());
            ps.setInt(3,  getCatID(book.getCategory()));
            ps.setString(4, book.getAuthor());
            ps.setString(5, book.getPublisher());
            ps.setString(6, book.getYear());
            ps.setString(7, book.getCopy());
            ps.executeUpdate();
            
            return true;
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }   
    }
    
    boolean updateBook(Book book){
        String query = "UPDATE `book_details`"
                    + "SET `title` = ?, `category_id` = ?, `author` = ?, `publisher` = ?, `publish_year` = ?, `copies` = ?"
                    + "WHERE `book_details`.`ISBN` = ?";
        PreparedStatement ps;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
           
            ps.setString(1, book.getTitle().substring(0, 1).toUpperCase() + book.getTitle().substring(1).toLowerCase());
            ps.setInt(2,  getCatID(book.getCategory()));
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            ps.setString(5, book.getYear());
            ps.setString(6, book.getCopy());
            ps.setString(7, book.getIsbn());
            ps.executeUpdate();
            
            return true;
        }catch(SQLException ex){
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }   
    }
    
    void deleteBook(String isbn){     
        String query = "DELETE FROM `book_details` WHERE `book_details`.`ISBN` = ?";
        PreparedStatement ps;
        
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, isbn);
            ps.executeUpdate();
          

        }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    DefaultComboBoxModel setCombo(boolean t, String query){
        DefaultComboBoxModel cm = new DefaultComboBoxModel();
        
        cm.addElement(t ? "All" : "--  SELECT  --");
        
        ResultSet rs;
        Statement st;
        try{      
            st = (Statement) openConnection().createStatement();
            rs = st.executeQuery(query);
            
            while(rs.next()) cm.addElement(rs.getString(2));
            
        }catch(SQLException ex){
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cm;
    }
    
    void addCategory(String cat){
        String query = "INSERT INTO `category_details`"
        + "(`category_id`, `category_name`)"
        + "VALUES (NULL, ?)";
        PreparedStatement ps;
       
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, cat);     
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Category Added !", "Success",2);
        }catch(SQLException ex){
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    void deleteCategory(String cat){      
        String query = "DELETE FROM `category_details` WHERE `category_details`.`category_name` = ?";
        PreparedStatement ps;
      
        try{
            ps = (PreparedStatement) openConnection().prepareStatement(query);
            ps.setString(1, cat);
            ps.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Category Deleted !", "Success",2);

        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex, "Engkkk",2);
        }   
    }
}