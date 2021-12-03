package library;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class Home extends javax.swing.JFrame {

    DBConnect db = new DBConnect();
    Color act = new Color(255, 51, 51);
    Color txtDis = new Color(102, 102, 102);

    Color dis = new Color(255, 255, 255);
    boolean dashStat = true, borrowStat = false, returnStat = false, bookStat = false, studentStat = false;
    
    public Home() {
        getRootPane().setBorder(BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51), 3));
        initComponents();
        init();     
        this.setTitle("LMS");
        windowListen();
        
    }
    
    public void init(){        
        bookTable.setModel(db.getBooks("SELECT * FROM `book_details`"));
        studentTable.setModel(db.getStudents("SELECT * FROM `student_details`"));
        borrowTable.setModel(db.getBorrowed("SELECT bd.date_borrowed, s.student_id, CONCAT_WS(', ', s.first_name, s.last_name) AS Fullname, b.ISBN, b.title FROM borrow_details bd, book_details b, student_details s WHERE s.student_id = bd.student_id && b.ISBN = bd.book_id;"));
        returnTable.setModel(db.getReturned("SELECT rh.date_borrow, rh.sid, CONCAT_WS(', ', s.first_name, s.last_name), rh.bid, b.title, rh.date_return FROM return_history rh, student_details s, book_details b WHERE rh.sid = s.student_id && rh.bid = b.ISBN ORDER BY rh.date_return DESC"));
        
        borrowPanel.setVisible(false);
        returnPanel.setVisible(false);
        bookPanel.setVisible(false);
        studentPanel.setVisible(false);
        
        transTable(bookTable);
        transTable(studentTable);
        transTable(borrowTable);
        transTable(returnTable);
       
        getCounts();
        category.setModel(db.setCombo(true, "SELECT * FROM `category_details`;"));
        course.setModel(db.setCombo(true, "SELECT * FROM `course_details`;"));
        
        course.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(course.getSelectedItem().toString().equals("All")){
                    studentTable.setModel(db.getStudents("SELECT * FROM `student_details`"));
                }else{
                    studentTable.setModel(db.getStudents("SELECT * FROM `student_details` WHERE course_id = '" + db.getCourseID((course.getSelectedItem().toString())) +"'"));
                }
            }
            
        });
        
        category.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(category.getSelectedItem().toString().equals("All")){
                    bookTable.setModel(db.getBooks("SELECT * FROM `book_details`"));
                }else{
                    bookTable.setModel(db.getBooks("SELECT * FROM `book_details` WHERE category_id = '" + db.getCatID((category.getSelectedItem().toString())) +"'"));
                }
            }
            
        });
        
        bookSearch.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) { check(); }
            @Override
            public void removeUpdate(DocumentEvent e) { check(); }
            @Override
            public void changedUpdate(DocumentEvent e) { check(); }
            
            public void check(){
                if(category.getSelectedItem().toString().equals("All")){
                    bookTable.setModel(db.getBooks("SELECT * FROM `book_details` WHERE title LIKE '%"+ bookSearch.getText() +"%'"));
                }else{
                    if(bookSearch.getText().equals("")){
                        bookTable.setModel(db.getBooks("SELECT * FROM `book_details` WHERE category_id = '" + db.getCatID(category.getSelectedItem().toString()) +"'"));
                    }else{              
                        bookTable.setModel(db.getBooks("SELECT * FROM `book_details` WHERE title LIKE '%"+ bookSearch.getText() +"%' && category_id = '" + db.getCatID(category.getSelectedItem().toString()) +"';"));

                    }
                }
            }
        });
        
        studentSearch.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) { check(); }
            @Override
            public void removeUpdate(DocumentEvent e) { check(); }
            @Override
            public void changedUpdate(DocumentEvent e) { check(); }
            
            public void check(){
                if(course.getSelectedItem().toString().equals("All")){
                    studentTable.setModel(db.getStudents("SELECT * FROM `student_details` WHERE first_name LIKE '%"+ studentSearch.getText() +"%'"));
                }else{
                    if(studentSearch.getText().equals("")){
                        studentTable.setModel(db.getStudents("SELECT * FROM `student_details` WHERE course_id = '" + db.getCourseID(course.getSelectedItem().toString()) +"'"));
                    }else{              
                        studentTable.setModel(db.getStudents("SELECT * FROM `student_details` WHERE first_name LIKE '%"+ studentSearch.getText() +"%' && course_id = '" + db.getCourseID(course.getSelectedItem().toString()) +"';"));

                    }
                }
            }
        });
    }
    
    void getCounts(){
        totalbook.setText(db.getBookCount());
        totalstudent.setText(db.getStudentCount());
        returncount.setText(db.getReturnCount());
        borrowcount.setText(db.getBorrowCount());
    }
    void transTable(JTable t){
        t.setRowHeight(35);
        t.setShowVerticalLines(false);
        
        ((DefaultTableCellRenderer)t.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        JTableHeader th = t.getTableHeader();
        th.setPreferredSize(new Dimension(0, 35));
        th.setForeground(Color.black);
        th.setBackground(Color.white);
        th.setReorderingAllowed(false);
        
    }
    
    void windowListen(){
        this.addWindowListener(new WindowListener(){
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            
            @Override
            public void windowActivated(WindowEvent e) {
                category.setModel(db.setCombo(true, "SELECT * FROM `category_details`;"));
                course.setModel(db.setCombo(true, "SELECT * FROM `course_details`;"));
                if(category.getSelectedItem().toString().equals("All")){
                    bookTable.setModel(db.getBooks("SELECT * FROM `book_details`"));
                }else{
                    bookTable.setModel(db.getBooks("SELECT * FROM `book_details` WHERE category_id = '" + db.getCatID((category.getSelectedItem().toString())) +"'"));
                }
                studentTable.setModel(db.getStudents("SELECT * FROM `student_details`"));
            }

            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        dashPanel = new javax.swing.JPanel();
        dashBtn = new javax.swing.JPanel();
        dash = new javax.swing.JLabel();
        borrowBtn = new javax.swing.JPanel();
        borrow = new javax.swing.JLabel();
        returnBtn = new javax.swing.JPanel();
        returntxt = new javax.swing.JLabel();
        studentsBtn = new javax.swing.JPanel();
        students = new javax.swing.JLabel();
        logoutBtn = new javax.swing.JPanel();
        out = new javax.swing.JLabel();
        booksBtn = new javax.swing.JPanel();
        books = new javax.swing.JLabel();
        panePanel = new javax.swing.JPanel();
        dashboardPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        totalbook = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        totalstudent = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        returncount = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        borrowcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        borrowPanel = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        stud_id = new javax.swing.JTextField();
        isbnBorrow1 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        borrowNow = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        borrowTable = new javax.swing.JTable();
        ipaste = new javax.swing.JButton();
        spaste = new javax.swing.JButton();
        returnPanel = new javax.swing.JPanel();
        jTextField11 = new javax.swing.JTextField();
        returnNow = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        sidreturn = new javax.swing.JTextField();
        isbnreturn = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        returnTable = new javax.swing.JTable();
        bookPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        bookTable = new javax.swing.JTable();
        category = new javax.swing.JComboBox<>();
        bookSearch = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        addBook = new javax.swing.JButton();
        addCategory = new javax.swing.JButton();
        studentPanel = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        studentSearch = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        course = new javax.swing.JComboBox<>();
        addCategory1 = new javax.swing.JButton();
        addBook1 = new javax.swing.JButton();
        headPanel = new javax.swing.JPanel();
        headHeader = new javax.swing.JPanel();
        exit = new javax.swing.JButton();
        mini = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        mainPanel.setBackground(new java.awt.Color(255, 102, 102));

        dashPanel.setBackground(new java.awt.Color(255, 255, 255));
        dashPanel.setForeground(new java.awt.Color(153, 255, 153));
        dashPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashBtn.setBackground(new java.awt.Color(255, 51, 51));
        dashBtn.setForeground(new java.awt.Color(255, 255, 255));
        dashBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dashBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                dashBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                dashBtnMouseExited(evt);
            }
        });

        dash.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        dash.setForeground(new java.awt.Color(255, 255, 255));
        dash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/dashboard.png"))); // NOI18N
        dash.setText("   Dashboard");

        javax.swing.GroupLayout dashBtnLayout = new javax.swing.GroupLayout(dashBtn);
        dashBtn.setLayout(dashBtnLayout);
        dashBtnLayout.setHorizontalGroup(
            dashBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashBtnLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(dash, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        dashBtnLayout.setVerticalGroup(
            dashBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dash, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        dashPanel.add(dashBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 60));

        borrowBtn.setBackground(new java.awt.Color(255, 255, 255));
        borrowBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                borrowBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                borrowBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                borrowBtnMouseExited(evt);
            }
        });

        borrow.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        borrow.setForeground(new java.awt.Color(102, 102, 102));
        borrow.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        borrow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/return.png"))); // NOI18N
        borrow.setText("   Borrowed");

        javax.swing.GroupLayout borrowBtnLayout = new javax.swing.GroupLayout(borrowBtn);
        borrowBtn.setLayout(borrowBtnLayout);
        borrowBtnLayout.setHorizontalGroup(
            borrowBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(borrowBtnLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(borrow, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        borrowBtnLayout.setVerticalGroup(
            borrowBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, borrowBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(borrow, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        dashPanel.add(borrowBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 255, 60));

        returnBtn.setBackground(new java.awt.Color(255, 255, 255));
        returnBtn.setToolTipText("");
        returnBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                returnBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                returnBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                returnBtnMouseExited(evt);
            }
        });

        returntxt.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        returntxt.setForeground(new java.awt.Color(102, 102, 102));
        returntxt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        returntxt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/return.png"))); // NOI18N
        returntxt.setText("   Returned");

        javax.swing.GroupLayout returnBtnLayout = new javax.swing.GroupLayout(returnBtn);
        returnBtn.setLayout(returnBtnLayout);
        returnBtnLayout.setHorizontalGroup(
            returnBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnBtnLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(returntxt, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        returnBtnLayout.setVerticalGroup(
            returnBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(returntxt, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        dashPanel.add(returnBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 255, 60));

        studentsBtn.setBackground(new java.awt.Color(255, 255, 255));
        studentsBtn.setToolTipText("");
        studentsBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                studentsBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                studentsBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                studentsBtnMouseExited(evt);
            }
        });

        students.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        students.setForeground(new java.awt.Color(102, 102, 102));
        students.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        students.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/student.png"))); // NOI18N
        students.setText("   Students");

        javax.swing.GroupLayout studentsBtnLayout = new javax.swing.GroupLayout(studentsBtn);
        studentsBtn.setLayout(studentsBtnLayout);
        studentsBtnLayout.setHorizontalGroup(
            studentsBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentsBtnLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(students, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        studentsBtnLayout.setVerticalGroup(
            studentsBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentsBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(students, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        dashPanel.add(studentsBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 255, 60));

        logoutBtn.setBackground(new java.awt.Color(255, 255, 255));
        logoutBtn.setToolTipText("");
        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtnMouseExited(evt);
            }
        });

        out.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        out.setForeground(new java.awt.Color(51, 51, 51));
        out.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        out.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/logout.png"))); // NOI18N
        out.setText("   Logout");

        javax.swing.GroupLayout logoutBtnLayout = new javax.swing.GroupLayout(logoutBtn);
        logoutBtn.setLayout(logoutBtnLayout);
        logoutBtnLayout.setHorizontalGroup(
            logoutBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(logoutBtnLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(out, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        logoutBtnLayout.setVerticalGroup(
            logoutBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, logoutBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(out, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        dashPanel.add(logoutBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 420, 255, 60));

        booksBtn.setBackground(new java.awt.Color(255, 255, 255));
        booksBtn.setToolTipText("");
        booksBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                booksBtnMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                booksBtnMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                booksBtnMouseExited(evt);
            }
        });

        books.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        books.setForeground(new java.awt.Color(102, 102, 102));
        books.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        books.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/book.png"))); // NOI18N
        books.setText("   Books");

        javax.swing.GroupLayout booksBtnLayout = new javax.swing.GroupLayout(booksBtn);
        booksBtn.setLayout(booksBtnLayout);
        booksBtnLayout.setHorizontalGroup(
            booksBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(booksBtnLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(books, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        booksBtnLayout.setVerticalGroup(
            booksBtnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, booksBtnLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(books, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addContainerGap())
        );

        dashPanel.add(booksBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 240, 255, 60));

        panePanel.setLayout(new javax.swing.OverlayLayout(panePanel));

        dashboardPanel.setBackground(new java.awt.Color(249, 239, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jPanel6.setBackground(new java.awt.Color(0, 204, 51));

        totalbook.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        totalbook.setForeground(new java.awt.Color(255, 255, 255));
        totalbook.setText("1000");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(totalbook)
                .addGap(19, 19, 19))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(80, Short.MAX_VALUE)
                .addComponent(totalbook)
                .addGap(27, 27, 27))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("Total Books");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(20, 20, 20))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jPanel7.setBackground(new java.awt.Color(153, 0, 153));

        totalstudent.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        totalstudent.setForeground(new java.awt.Color(255, 255, 255));
        totalstudent.setText("1000");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(totalstudent)
                .addGap(21, 21, 21))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(82, Short.MAX_VALUE)
                .addComponent(totalstudent)
                .addGap(24, 24, 24))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(51, 51, 51));
        jLabel3.setText("Total Students");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(21, 21, 21))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jPanel8.setBackground(new java.awt.Color(255, 153, 51));

        returncount.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        returncount.setForeground(new java.awt.Color(255, 255, 255));
        returncount.setText("1000");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returncount)
                .addGap(19, 19, 19))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(96, Short.MAX_VALUE)
                .addComponent(returncount)
                .addGap(22, 22, 22))
        );

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Returned Today");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel5)
                .addContainerGap(90, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(19, 19, 19))
        );

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 2, true));

        jPanel9.setBackground(new java.awt.Color(102, 102, 255));

        borrowcount.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        borrowcount.setForeground(new java.awt.Color(255, 255, 255));
        borrowcount.setText("1000");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(borrowcount)
                .addGap(19, 19, 19))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(borrowcount)
                .addGap(24, 24, 24))
        );

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setText("Borrowed Today");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel7)
                .addContainerGap(99, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout dashboardPanelLayout = new javax.swing.GroupLayout(dashboardPanel);
        dashboardPanel.setLayout(dashboardPanelLayout);
        dashboardPanelLayout.setHorizontalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dashboardPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(30, 30, 30)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        dashboardPanelLayout.setVerticalGroup(
            dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dashboardPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addGroup(dashboardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        panePanel.add(dashboardPanel);

        borrowPanel.setBackground(new java.awt.Color(249, 239, 255));

        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setText("ISBN 1");

        jLabel23.setForeground(new java.awt.Color(0, 0, 0));
        jLabel23.setText("Student ID");

        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setText("Search :");

        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setText("ISBN 2");

        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("ISBN 3");

        borrowNow.setBackground(new java.awt.Color(255, 153, 51));
        borrowNow.setForeground(new java.awt.Color(255, 255, 255));
        borrowNow.setText("Borrow");
        borrowNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrowNowActionPerformed(evt);
            }
        });

        borrowTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        borrowTable.getTableHeader().setReorderingAllowed(false);
        borrowTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                borrowTableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(borrowTable);

        ipaste.setBackground(new java.awt.Color(255, 255, 255));
        ipaste.setForeground(new java.awt.Color(0, 0, 0));
        ipaste.setText("Paste");
        ipaste.setBorder(null);
        ipaste.setFocusPainted(false);
        ipaste.setFocusable(false);
        ipaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipasteActionPerformed(evt);
            }
        });

        spaste.setBackground(new java.awt.Color(255, 255, 255));
        spaste.setForeground(new java.awt.Color(0, 0, 0));
        spaste.setText("Paste");
        spaste.setBorder(null);
        spaste.setFocusPainted(false);
        spaste.setFocusable(false);
        spaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spasteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout borrowPanelLayout = new javax.swing.GroupLayout(borrowPanel);
        borrowPanel.setLayout(borrowPanelLayout);
        borrowPanelLayout.setHorizontalGroup(
            borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(borrowPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22))
                .addGap(18, 18, 18)
                .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(borrowPanelLayout.createSequentialGroup()
                        .addComponent(stud_id, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spaste))
                    .addGroup(borrowPanelLayout.createSequentialGroup()
                        .addComponent(isbnBorrow1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ipaste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(borrowPanelLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(borrowPanelLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(borrowPanelLayout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                    .addComponent(borrowNow, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(borrowPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        borrowPanelLayout.setVerticalGroup(
            borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, borrowPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stud_id, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(spaste, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(borrowNow))
                .addGap(18, 18, 18)
                .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(isbnBorrow1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26)
                        .addComponent(jLabel22)
                        .addComponent(ipaste, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(379, Short.MAX_VALUE))
            .addGroup(borrowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(borrowPanelLayout.createSequentialGroup()
                    .addGap(125, 125, 125)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        panePanel.add(borrowPanel);

        returnPanel.setBackground(new java.awt.Color(249, 239, 255));

        returnNow.setBackground(new java.awt.Color(255, 153, 51));
        returnNow.setForeground(new java.awt.Color(255, 255, 255));
        returnNow.setText("Return");
        returnNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnNowActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setText("Search :");

        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setText("ISBN");

        jLabel31.setForeground(new java.awt.Color(0, 0, 0));
        jLabel31.setText("Student ID");

        returnTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        returnTable.getTableHeader().setReorderingAllowed(false);
        returnTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                returnTableMouseClicked(evt);
            }
        });
        jScrollPane8.setViewportView(returnTable);

        javax.swing.GroupLayout returnPanelLayout = new javax.swing.GroupLayout(returnPanel);
        returnPanel.setLayout(returnPanelLayout);
        returnPanelLayout.setHorizontalGroup(
            returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnPanelLayout.createSequentialGroup()
                .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel31)
                            .addComponent(jLabel30))
                        .addGap(18, 18, 18)
                        .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(returnPanelLayout.createSequentialGroup()
                                .addComponent(isbnreturn, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(returnNow, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(124, 124, 124)
                                .addComponent(jLabel27)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField11, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                            .addComponent(sidreturn, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(returnPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)))
                .addContainerGap())
        );
        returnPanelLayout.setVerticalGroup(
            returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sidreturn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(18, 18, 18)
                .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(isbnreturn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30)
                        .addComponent(returnNow))
                    .addGroup(returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addContainerGap())
        );

        panePanel.add(returnPanel);

        bookPanel.setBackground(new java.awt.Color(249, 239, 255));

        bookTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        bookTable.getTableHeader().setReorderingAllowed(false);
        bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(bookTable);

        category.setBackground(new java.awt.Color(51, 102, 255));
        category.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        category.setForeground(new java.awt.Color(255, 255, 255));
        category.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { }));
        category.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        category.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        category.setFocusable(false);
        category.setRequestFocusEnabled(false);

        bookSearch.setBackground(new java.awt.Color(255, 255, 255));
        bookSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        bookSearch.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        bookSearch.setName(""); // NOI18N

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setText("Search :");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel14.setText("Category :");

        addBook.setBackground(new java.awt.Color(0, 204, 51));
        addBook.setForeground(new java.awt.Color(255, 255, 255));
        addBook.setText("Add Book");
        addBook.setFocusPainted(false);
        addBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookActionPerformed(evt);
            }
        });

        addCategory.setBackground(new java.awt.Color(249, 239, 255));
        addCategory.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addCategory.setForeground(new java.awt.Color(255, 255, 255));
        addCategory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/plus.png"))); // NOI18N
        addCategory.setBorder(null);
        addCategory.setFocusable(false);
        addCategory.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addCategory.setIconTextGap(0);
        addCategory.setPreferredSize(new java.awt.Dimension(20, 20));
        addCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCategoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout bookPanelLayout = new javax.swing.GroupLayout(bookPanel);
        bookPanel.setLayout(bookPanelLayout);
        bookPanelLayout.setHorizontalGroup(
            bookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bookPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addGroup(bookPanelLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bookSearch)
                        .addGap(33, 33, 33)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(addCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(addBook)))
                .addContainerGap())
        );
        bookPanelLayout.setVerticalGroup(
            bookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bookPanelLayout.createSequentialGroup()
                .addGroup(bookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bookPanelLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(bookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(category, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(bookSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bookPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(bookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addBook, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addCategory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                .addContainerGap())
        );

        panePanel.add(bookPanel);

        studentPanel.setBackground(new java.awt.Color(249, 239, 255));

        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        studentTable.getTableHeader().setReorderingAllowed(false);
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                studentTableMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(studentTable);

        studentSearch.setBackground(new java.awt.Color(255, 255, 255));
        studentSearch.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        studentSearch.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        studentSearch.setName(""); // NOI18N

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel15.setText("Search :");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setText("Course :");

        course.setBackground(new java.awt.Color(51, 102, 255));
        course.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        course.setForeground(new java.awt.Color(255, 255, 255));
        course.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { }));
        course.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        course.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        course.setFocusable(false);
        course.setRequestFocusEnabled(false);

        addCategory1.setBackground(new java.awt.Color(249, 239, 255));
        addCategory1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addCategory1.setForeground(new java.awt.Color(255, 255, 255));
        addCategory1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/library/images/plus.png"))); // NOI18N
        addCategory1.setBorder(null);
        addCategory1.setFocusable(false);

        addBook1.setBackground(new java.awt.Color(153, 0, 153));
        addBook1.setForeground(new java.awt.Color(255, 255, 255));
        addBook1.setText("Add Student");
        addBook1.setFocusPainted(false);
        addBook1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBook1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout studentPanelLayout = new javax.swing.GroupLayout(studentPanel);
        studentPanel.setLayout(studentPanelLayout);
        studentPanelLayout.setHorizontalGroup(
            studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentPanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(studentSearch)
                        .addGap(33, 33, 33)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(course, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(addCategory1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(addBook1)))
                .addContainerGap())
        );
        studentPanelLayout.setVerticalGroup(
            studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, studentPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addBook1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(studentPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(course, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(studentSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addCategory1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addContainerGap())
        );

        panePanel.add(studentPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(dashPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(panePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 703, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dashPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        headPanel.setBackground(new java.awt.Color(255, 0, 51));

        headHeader.setBackground(new java.awt.Color(255, 255, 255));

        exit.setBackground(new java.awt.Color(255, 255, 255));
        exit.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        exit.setText("x");
        exit.setBorderPainted(false);
        exit.setFocusable(false);
        exit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitMouseExited(evt);
            }
        });
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });

        mini.setBackground(new java.awt.Color(255, 255, 255));
        mini.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        mini.setText("-");
        mini.setBorderPainted(false);
        mini.setFocusable(false);
        mini.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                miniMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                miniMouseExited(evt);
            }
        });
        mini.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miniActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout headHeaderLayout = new javax.swing.GroupLayout(headHeader);
        headHeader.setLayout(headHeaderLayout);
        headHeaderLayout.setHorizontalGroup(
            headHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headHeaderLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(mini)
                .addGap(0, 0, 0)
                .addComponent(exit))
        );
        headHeaderLayout.setVerticalGroup(
            headHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mini, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel8.setFont(new java.awt.Font("Montserrat Extra Bold", 1, 36)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("LIBRARY MANAGEMENT SYSTEM");
        jLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout headPanelLayout = new javax.swing.GroupLayout(headPanel);
        headPanel.setLayout(headPanelLayout);
        headPanelLayout.setHorizontalGroup(
            headPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(headHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(headPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 924, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        headPanelLayout.setVerticalGroup(
            headPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headPanelLayout.createSequentialGroup()
                .addComponent(headHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(headPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(headPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    void trans(JPanel j1, JPanel j2, JPanel j3, JPanel j4, JPanel j5){
        j1.setVisible(false);
        j2.setVisible(false);
        j3.setVisible(false);
        j4.setVisible(false);
        j5.setVisible(true);
    }
    void change(JPanel b1, JPanel b2, JPanel b3, JPanel b4, JPanel b5, JLabel l1, JLabel l2, JLabel l3, JLabel l4, JLabel l5){
        b1.setBackground(act);
        b2.setBackground(dis);
        b3.setBackground(dis);
        b4.setBackground(dis);
        b5.setBackground(dis);
        l1.setFont(new java.awt.Font("Segoe UI", 1, 14));
        l1.setForeground(new java.awt.Color(255, 255, 255));
        l2.setForeground(txtDis);
        l3.setForeground(txtDis);
        l4.setForeground(txtDis);
        l5.setForeground(txtDis);
    }
    void deac(){
        dash.setFont(new java.awt.Font("Segoe UI", 1, 12));
        borrow.setFont(new java.awt.Font("Segoe UI", 1, 12));
        returntxt.setFont(new java.awt.Font("Segoe UI", 1, 12));
        students.setFont(new java.awt.Font("Segoe UI", 1, 12));
        books.setFont(new java.awt.Font("Segoe UI", 1, 12));
        dashStat = false;
        borrowStat = false;
        returnStat = false;
        bookStat = false;
        studentStat = false;
    }
    private void dashBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashBtnMouseClicked
       getCounts();
       deac();
       dashStat = true;
       trans(studentPanel, borrowPanel, returnPanel,  bookPanel, dashboardPanel);
       change(dashBtn,  booksBtn, borrowBtn, returnBtn, studentsBtn, dash, borrow, returntxt, students, books);  
       this.setTitle("Dashboard");
    }//GEN-LAST:event_dashBtnMouseClicked
    
    private void borrowBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_borrowBtnMouseClicked
        borrowTable.setModel(db.getBorrowed("SELECT bd.date_borrowed, s.student_id, CONCAT_WS(', ', s.first_name, s.last_name) AS Fullname, b.ISBN, b.title FROM borrow_details bd, book_details b, student_details s WHERE s.student_id = bd.student_id && b.ISBN = bd.book_id;"));
  
        deac();
        borrowStat = true;
        trans(studentPanel, dashboardPanel, returnPanel,  bookPanel, borrowPanel);
        change(borrowBtn, booksBtn, dashBtn, returnBtn, studentsBtn, borrow, dash, returntxt, students, books);
        this.setTitle("Borrow Book");
    }//GEN-LAST:event_borrowBtnMouseClicked

    private void returnBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnBtnMouseClicked
        returnTable.setModel(db.getReturned("SELECT rh.date_borrow, rh.sid, CONCAT_WS(', ', s.first_name, s.last_name), rh.bid, b.title, rh.date_return FROM return_history rh, student_details s, book_details b WHERE rh.sid = s.student_id && rh.bid = b.ISBN ORDER BY rh.date_return DESC"));
        
        deac();
        returnStat = true;
        trans(studentPanel, borrowPanel, dashboardPanel, bookPanel, returnPanel);
        change(returnBtn, booksBtn, dashBtn, borrowBtn, studentsBtn, returntxt, dash, borrow, students, books);
        this.setTitle("Return Book");
    }//GEN-LAST:event_returnBtnMouseClicked

    private void studentsBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentsBtnMouseClicked
        deac();
        studentStat = true;
        trans(borrowPanel, returnPanel,  dashboardPanel, bookPanel, studentPanel);
        change(studentsBtn, booksBtn, dashBtn, returnBtn, borrowBtn, students, dash, returntxt, borrow, books);
        this.setTitle("Student List");
    }//GEN-LAST:event_studentsBtnMouseClicked

    private void logoutBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseClicked
        int log = JOptionPane.showConfirmDialog(null,"Are you sure to logout ?","Logout",0,2);
        if(log==0){  
            this.setVisible(false);
            this.dispose();
            new Login().setVisible(true);
        }
        
    }//GEN-LAST:event_logoutBtnMouseClicked

    private void borrowBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_borrowBtnMouseEntered
        borrow.setFont(new java.awt.Font("Segoe UI", 1, 14));
    }//GEN-LAST:event_borrowBtnMouseEntered

    private void borrowBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_borrowBtnMouseExited
        if(!borrowStat) borrow.setFont(new java.awt.Font("Segoe UI", 1, 12));
    }//GEN-LAST:event_borrowBtnMouseExited

    private void returnBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnBtnMouseEntered
        returntxt.setFont(new java.awt.Font("Segoe UI", 1, 14));
    }//GEN-LAST:event_returnBtnMouseEntered

    private void returnBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnBtnMouseExited
        if(!returnStat) returntxt.setFont(new java.awt.Font("Segoe UI", 1, 12));
    }//GEN-LAST:event_returnBtnMouseExited

    private void studentsBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentsBtnMouseEntered
        students.setFont(new java.awt.Font("Segoe UI", 1, 14));
    }//GEN-LAST:event_studentsBtnMouseEntered

    private void studentsBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentsBtnMouseExited
        if(!studentStat) students.setFont(new java.awt.Font("Segoe UI", 1, 12));
    }//GEN-LAST:event_studentsBtnMouseExited

    private void dashBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashBtnMouseEntered
        dash.setFont(new java.awt.Font("Segoe UI", 1, 14));
    }//GEN-LAST:event_dashBtnMouseEntered

    private void dashBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dashBtnMouseExited
        if(!dashStat) dash.setFont(new java.awt.Font("Segoe UI", 1, 12));
    }//GEN-LAST:event_dashBtnMouseExited

    private void logoutBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseEntered
        out.setFont(new java.awt.Font("Segoe UI", 1, 14));
        logoutBtn.setBackground(new java.awt.Color(255, 51, 51));
    }//GEN-LAST:event_logoutBtnMouseEntered

    private void logoutBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutBtnMouseExited
        out.setFont(new java.awt.Font("Segoe UI", 1, 12));
        logoutBtn.setBackground(dis);
    }//GEN-LAST:event_logoutBtnMouseExited

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        int log = JOptionPane.showConfirmDialog(null,"Are you sure to exit ?","Logout",0,2);
        if(log==0){  
            System.exit(0);
        }
    }//GEN-LAST:event_exitActionPerformed

    private void miniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miniActionPerformed
        this.setState(ICONIFIED);
    }//GEN-LAST:event_miniActionPerformed

    private void miniMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_miniMouseEntered
        mini.setBackground(new java.awt.Color(255, 51, 51));
        mini.setForeground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_miniMouseEntered

    private void miniMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_miniMouseExited
        mini.setBackground(new java.awt.Color(255, 255, 255));
        mini.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_miniMouseExited

    private void exitMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseEntered
        exit.setBackground(new java.awt.Color(255, 51, 51));
        exit.setForeground(new java.awt.Color(255, 255, 255));
    }//GEN-LAST:event_exitMouseEntered

    private void exitMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exitMouseExited
        exit.setBackground(new java.awt.Color(255, 255, 255));
        exit.setForeground(new java.awt.Color(0, 0, 0));
    }//GEN-LAST:event_exitMouseExited

    private void booksBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_booksBtnMouseClicked
       deac();
       bookStat = true;
       trans(studentPanel, borrowPanel, returnPanel, dashboardPanel, bookPanel);
       change(booksBtn, dashBtn, borrowBtn, returnBtn, studentsBtn, books, dash, borrow, returntxt, students); 
       this.setTitle("Book List");
    }//GEN-LAST:event_booksBtnMouseClicked

    private void booksBtnMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_booksBtnMouseEntered
        books.setFont(new java.awt.Font("Segoe UI", 1, 14));    
    }//GEN-LAST:event_booksBtnMouseEntered

    private void booksBtnMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_booksBtnMouseExited
        if(!bookStat) books.setFont(new java.awt.Font("Segoe UI", 1, 12));
    }//GEN-LAST:event_booksBtnMouseExited

    private void addBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBookActionPerformed
        new AddBook().setVisible(true);
    }//GEN-LAST:event_addBookActionPerformed

    private void bookTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookTableMouseClicked
        if(evt.getButton() == java.awt.event.MouseEvent.BUTTON1){
            DefaultTableModel model = (DefaultTableModel) bookTable.getModel();

            int i = bookTable.getSelectedRow();
            Book book = new Book();
            book.setIsbn(model.getValueAt(i ,0).toString());
            book.setTitle(model.getValueAt(i ,1).toString());
            book.setCategory(model.getValueAt(i ,2).toString());
            book.setAuthor(model.getValueAt(i ,3).toString());
            book.setPublisher(model.getValueAt(i ,4).toString());
            book.setYear(model.getValueAt(i ,5).toString());
            book.setCopy(model.getValueAt(i ,6).toString());
            new AddBook(book).setVisible(true);
            isbnBorrow1.setText(model.getValueAt(i ,0).toString());
        }
    }//GEN-LAST:event_bookTableMouseClicked
    
    private void addCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryActionPerformed
        new AddCategory().setVisible(true);
    }//GEN-LAST:event_addCategoryActionPerformed

    private void studentTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentTableMouseClicked
        if(evt.getButton() == java.awt.event.MouseEvent.BUTTON1){
            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            int i = studentTable.getSelectedRow();
            Student student = new Student();
            student.setStudentID(model.getValueAt(i ,0).toString());
            student.setFname(model.getValueAt(i ,1).toString());
            student.setLname(model.getValueAt(i ,2).toString());
            student.setCourse(model.getValueAt(i ,3).toString());
            student.setGender(model.getValueAt(i ,4).toString());
            student.setEmail(model.getValueAt(i ,5).toString());
            student.setNumber(model.getValueAt(i ,6).toString());
            new AddStudent(student).setVisible(true);
            stud_id.setText(model.getValueAt(i ,0).toString());
        }
    }//GEN-LAST:event_studentTableMouseClicked

    private void addBook1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBook1ActionPerformed
        new AddStudent().setVisible(true);
    }//GEN-LAST:event_addBook1ActionPerformed

    private void borrowTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_borrowTableMouseClicked
        if(evt.getButton() == java.awt.event.MouseEvent.BUTTON1){
            DefaultTableModel model = (DefaultTableModel) borrowTable.getModel();
            int i = borrowTable.getSelectedRow();
           
            sidreturn.setText(model.getValueAt(i ,1).toString());
            isbnreturn.setText(model.getValueAt(i ,3).toString());
            
            deac();
            returnStat = true;
            trans(studentPanel, borrowPanel, dashboardPanel, bookPanel, returnPanel);
            change(returnBtn, booksBtn, dashBtn, borrowBtn, studentsBtn, returntxt, dash, borrow, students, books);
            this.setTitle("Return Book");
        }
        
    }//GEN-LAST:event_borrowTableMouseClicked

    private void returnTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_returnTableMouseClicked
        
    }//GEN-LAST:event_returnTableMouseClicked

    private void borrowNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrowNowActionPerformed
        if(db.insertBorrow(stud_id.getText(), isbnBorrow1.getText())){
            isbnBorrow1.setText("");
            stud_id.setText("");
            JOptionPane.showMessageDialog(null, "Borrowed Successfully !", "Success",2);
            borrowTable.setModel(db.getBorrowed("SELECT bd.date_borrowed, s.student_id, CONCAT_WS(', ', s.first_name, s.last_name) AS Fullname, b.ISBN, b.title FROM borrow_details bd, book_details b, student_details s WHERE s.student_id = bd.student_id && b.ISBN = bd.book_id;"));      
        }
    }//GEN-LAST:event_borrowNowActionPerformed

    private void returnNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnNowActionPerformed
        if(db.returnBook(sidreturn.getText(), isbnreturn.getText())){         
            JOptionPane.showMessageDialog(null, "Returned Successfully !", "Done",2);
            returnTable.setModel(db.getReturned("SELECT rh.date_borrow, rh.sid, CONCAT(s.first_name, s.last_name), rh.bid, b.title, rh.date_return FROM return_history rh, student_details s, book_details b WHERE rh.sid = s.student_id && rh.bid = b.ISBN"));
            sidreturn.setText("");
            isbnreturn.setText("");
            
        }else{
            JOptionPane.showMessageDialog(null, "Engkkkk !", "Error",2);
        }
    }//GEN-LAST:event_returnNowActionPerformed

    private void ipasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipasteActionPerformed
        if(isbnBorrow1.getText().equals("")){
            try {
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                DataFlavor dataFlavor = DataFlavor.stringFlavor;
                isbnBorrow1.setText(c.getData(dataFlavor).toString());
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_ipasteActionPerformed

    private void spasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spasteActionPerformed
        if(stud_id.getText().equals("")){
            try {
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                DataFlavor dataFlavor = DataFlavor.stringFlavor;
                stud_id.setText(c.getData(dataFlavor).toString());
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_spasteActionPerformed
    
   
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Home().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBook;
    private javax.swing.JButton addBook1;
    private javax.swing.JButton addCategory;
    private javax.swing.JButton addCategory1;
    private javax.swing.JPanel bookPanel;
    private javax.swing.JTextField bookSearch;
    private javax.swing.JTable bookTable;
    private javax.swing.JLabel books;
    private javax.swing.JPanel booksBtn;
    private javax.swing.JLabel borrow;
    private javax.swing.JPanel borrowBtn;
    private javax.swing.JButton borrowNow;
    private javax.swing.JPanel borrowPanel;
    private javax.swing.JTable borrowTable;
    private javax.swing.JLabel borrowcount;
    private javax.swing.JComboBox<String> category;
    private javax.swing.JComboBox<String> course;
    private javax.swing.JLabel dash;
    private javax.swing.JPanel dashBtn;
    private javax.swing.JPanel dashPanel;
    private javax.swing.JPanel dashboardPanel;
    private javax.swing.JButton exit;
    private javax.swing.JPanel headHeader;
    private javax.swing.JPanel headPanel;
    private javax.swing.JButton ipaste;
    private javax.swing.JTextField isbnBorrow1;
    private javax.swing.JTextField isbnreturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JPanel logoutBtn;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton mini;
    private javax.swing.JLabel out;
    private javax.swing.JPanel panePanel;
    private javax.swing.JPanel returnBtn;
    private javax.swing.JButton returnNow;
    private javax.swing.JPanel returnPanel;
    private javax.swing.JTable returnTable;
    private javax.swing.JLabel returncount;
    private javax.swing.JLabel returntxt;
    private javax.swing.JTextField sidreturn;
    private javax.swing.JButton spaste;
    private javax.swing.JTextField stud_id;
    private javax.swing.JPanel studentPanel;
    private javax.swing.JTextField studentSearch;
    private javax.swing.JTable studentTable;
    private javax.swing.JLabel students;
    private javax.swing.JPanel studentsBtn;
    private javax.swing.JLabel totalbook;
    private javax.swing.JLabel totalstudent;
    // End of variables declaration//GEN-END:variables
}