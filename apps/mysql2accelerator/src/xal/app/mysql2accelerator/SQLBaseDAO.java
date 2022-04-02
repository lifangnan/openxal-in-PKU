package xal.app.mysql2accelerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import xal.app.diagtiming.ConnectAndSetPVs;

public class SQLBaseDAO {

	private static final String DRIVER="com.mysql.cj.jdbc.Driver";
	private String URL="jdbc:mysql://localhost:3306/clapa?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	
	private String IP = "localhost";
	private String PORT = "3306";
	private String USERNAME = "root";
	private String PASSWORD="laser";
	private String DBNAME = "clapa";
	private String TABLENAME = "proton1";
	
	private boolean ConnectionAvailable = false;
	
	private Connection conn = null;//连接对象
	protected PreparedStatement pst;//预编译的preparedstatement对象
	protected ResultSet rs; //结果集resultset对象 
	
	
	public SQLBaseDAO(String Ip, String Port, String Username, String Password, String DBName) {
		IP = Ip;
		PORT = Port;
		USERNAME = Username;
		PASSWORD = Password;
		DBNAME = DBName;
		URL = "jdbc:mysql://"+ IP  +":" + PORT +"/"+ DBNAME +"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	}
	
	public SQLBaseDAO() {
		
	}
	
	public boolean isConnectionAvailable() {
		return ConnectionAvailable;
	}
	
	//获取数据库的连接
	public void getConnection(){
		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
			ConnectionAvailable = true;
		} catch (ClassNotFoundException e) {
			ConnectionAvailable = false;
			e.printStackTrace();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "可能提供了错误的数据库信息，或无法连接数据库", "Warning", JOptionPane.WARNING_MESSAGE);
			ConnectionAvailable = false;
			e.printStackTrace();
		}
	}
	
	//释放资源
	public void closeConnection(){
		//释放结果集
		if(rs!=null){
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
		if(pst!=null){
		try {
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
		//释放连接
		if(conn!=null){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	
	}

	//增删改
	public int executeUpdate(String sql,String []paras){
		int count=0;
		try {
			this.getConnection();//连接数据库
			pst=conn.prepareStatement(sql);//基于sql语句创建预编译对象
			//如果paras不为空或者长度大于0 就赋值
			if(paras!=null && paras.length>0){
				for (int i = 0; i < paras.length; i++) {
					pst.setString(i+1, paras[i]);
				}
			}
			count=pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			closeConnection();
		}
		return count;
	}
	
	
	//查询
	public ResultSet executeQuery(String sql,String[]paras){
		try {
			this.getConnection();//连接数据库
			pst=conn.prepareStatement(sql);//基于sql语句创建预编译对象
			//如果paras不为空或者长度大于0 就赋值
			if(paras!=null && paras.length>0){
				int index=1;
				for (String para : paras) {
					pst.setString(index,para);
					index++;
				}
			}
			rs=pst.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
}