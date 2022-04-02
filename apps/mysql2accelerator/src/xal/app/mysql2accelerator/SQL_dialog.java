package xal.app.mysql2accelerator;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class SQL_dialog extends JDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection conn = null;
	private Statement statement = null;
	ResultSet rSet = null;
	ResultSetMetaData sqlMetaData = null;
	
	String IP, PORT, USER, PASS, DBNAME, TABLENAME;
	
	// Outer controllers
	public SQLControllerModel sqlControllerModel = null;
	public Mysql2acceleratorController MainController = null;
	
	
	final JLabel IPLabel = new JLabel("MySQL IP:");
	final JLabel portJLabel = new JLabel("Port");
	final JLabel usernameJLabel = new JLabel("Username:");
	final JLabel passwordJLabel = new JLabel("Password:");
	final JLabel DatabasenameJLabel = new JLabel("Database name:");
	final JLabel TablenameJLabel = new JLabel("Table name:");
	JTextField  iPTextField = new JTextField("localhost");
	JTextField  portTextField = new JTextField("3306");
	JTextField  usernameTextField = new JTextField("root");
	JPasswordField  passwordTextField = new JPasswordField("laser");
	JTextField  DatabasenameTextField = new JTextField("clapa");
	JTextField  TablenameTextField = new JTextField("proton1");
	
	SQL_dialog(SQLControllerModel sqlController, Mysql2acceleratorController mainController){
		MainController = mainController;
		sqlControllerModel = sqlController;
		
		this.setTitle("Link to MySQL");
        this.setVisible(false);
//        this.setLocation(200,200);
        this.setSize(500,300);
        this.setLocationRelativeTo(null); 
        
        //add one label
//        Container contentPane = this.getContentPane();
        
        // 取消布局
        this.setLayout(null);
            
        IPLabel.setBounds(10, 10, 100, 20);
        this.add(IPLabel);
                
        portJLabel.setBounds(10, 40, 100, 20);
        this.add(portJLabel);
                
        usernameJLabel.setBounds(10, 70, 100, 20);
        this.add(usernameJLabel); 
        
        passwordJLabel.setBounds(10, 100, 100, 20);
        this.add(passwordJLabel);        
        
        DatabasenameJLabel.setBounds(10, 130, 100, 20);
        this.add(DatabasenameJLabel);        
        
        TablenameJLabel.setBounds(10, 160, 100, 20);
        this.add(TablenameJLabel);        
        
        iPTextField.setBounds(110, 10, 300, 20);
        this.add(iPTextField);        
        
        portTextField.setBounds(110, 40, 300, 20);
        this.add(portTextField);        
        
        usernameTextField.setBounds(110, 70, 300, 20);
        this.add(usernameTextField);
                
        passwordTextField.setBounds(110, 100, 300, 20);
        this.add(passwordTextField);        
        
        DatabasenameTextField.setBounds(110, 130, 300, 20);
        this.add(DatabasenameTextField);        
        
        TablenameTextField.setBounds(110, 160, 300, 20);
        this.add(TablenameTextField);
        
        
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener( new ConfirmButtonListener() );
        confirmButton.setBounds(90, 190, 100, 30);
        this.add(confirmButton);
        
        updateValues();
	}
	
	
	class ConfirmButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Update Values
			updateValues();
			
//			SQLBaseDAO sqldao = new SQLBaseDAO(IP, PORT, USER, PASS, DBNAME);
//			sqldao.getConnection();
//			rSet = sqldao.executeQuery("Select * From " + TABLENAME, null);
//			sqldao.closeConnection();
			SQL_dialog.this.sqlControllerModel.getSQLConnection();
			SQL_dialog.this.MainController.updateDatabaseView();
			
			SQL_dialog.this.setVisible(false);
		}
		
	}
	
	private void updateValues() {
		IP = iPTextField.getText();
		PORT = portTextField.getText();
		USER = usernameTextField.getText();
		PASS = new String( passwordTextField.getPassword() );
		DBNAME = DatabasenameTextField.getText();
		TABLENAME = TablenameTextField.getText();
	}
	
	
	
	
	public ResultSet getResultSet() {
		return rSet;
	}
	
//	public Connection getConnection() {
//		return connection;
//	}
}
