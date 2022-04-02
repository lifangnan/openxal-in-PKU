package xal.app.mysql2accelerator;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JOptionPane;

public class SQLControllerModel {
	public SQL_dialog sql_dialog = null;
	Mysql2acceleratorController MainController = null;
	
	SQLBaseDAO sqldao = null;
	ResultSet rSet = null;
	ResultSetMetaData sqlMetaData = null;
	Vector<String> columnNames = null;
	Vector<Vector> tableData = null;
	
	String IP, PORT, USER, PASS, DBNAME, TABLENAME;
	
	public SQLControllerModel(Mysql2acceleratorController mainController) {
		MainController = mainController;
		sql_dialog = new SQL_dialog(this, MainController);
	}
	
	public void getSQLConnection() {
		// Get IP, PORT, USER, PASS, DBNAME, TABLENAME from sql_dialog
		updateValues();
		try {
			sqldao = new SQLBaseDAO(IP, PORT, USER, PASS, DBNAME);
			sqldao.getConnection();
		} catch (Exception e) {  
			System.out.println("Fail to connect to SQL database! Please check the database information your provided.");
		}
		
	}
	
	public boolean isConnectionPrepared() {
		if(sqldao == null) {
			return false;
		}else {
			return sqldao.isConnectionAvailable();
		}
	}
	
	public void closeSQLConnection() {
		sqldao.closeConnection();
	}
	
	public Vector<String> getColumnNames() {
		columnNames = new Vector<String>();
		
		if(sqldao == null) {
			getSQLConnection();
		}
		rSet = sqldao.executeQuery("Select * From " + TABLENAME, null);
		try {
			sqlMetaData = rSet.getMetaData();
			int columncount = sqlMetaData.getColumnCount();
			for(int i = 1; i <= columncount; i++) {
				columnNames.add( sqlMetaData.getColumnLabel(i) );
//				System.out.println(sqlMetaData.getColumnLabel(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return columnNames;
	}
	
	public Vector<Vector> getTableData() {
		tableData = new Vector<Vector>();
		
		if(sqldao == null) {
			getSQLConnection();
		}
		if(rSet == null) {
			rSet = sqldao.executeQuery("Select * From " + TABLENAME, null);
		}
		if(columnNames == null) {
			this.getColumnNames();
		} 
		
		try {
			sqlMetaData = rSet.getMetaData();
			int columncount = sqlMetaData.getColumnCount();
			
			while(rSet.next()){
				Vector oneRow = new Vector();
				for(int i = 1; i <= columncount; i++) {
					oneRow.add( rSet.getObject(i) );
//					System.out.println( rSet.getObject(i) );
				}
				tableData.add(oneRow);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return tableData;
	}
	
	private void updateValues() {
		if( sql_dialog == null ) {
			System.out.println("SQL dialog does not exist.");
		}else {
			IP = sql_dialog.IP;
			PORT = sql_dialog.PORT;
			USER = sql_dialog.USER;
			PASS = sql_dialog.PASS;
			DBNAME = sql_dialog.DBNAME;
			TABLENAME = sql_dialog.TABLENAME;
		}
	}
}
