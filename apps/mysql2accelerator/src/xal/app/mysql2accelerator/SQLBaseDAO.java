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
	
	private Connection conn = null;//���Ӷ���
	protected PreparedStatement pst;//Ԥ�����preparedstatement����
	protected ResultSet rs; //�����resultset���� 
	
	
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
	
	//��ȡ���ݿ������
	public void getConnection(){
		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
			ConnectionAvailable = true;
		} catch (ClassNotFoundException e) {
			ConnectionAvailable = false;
			e.printStackTrace();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "�����ṩ�˴�������ݿ���Ϣ�����޷��������ݿ�", "Warning", JOptionPane.WARNING_MESSAGE);
			ConnectionAvailable = false;
			e.printStackTrace();
		}
	}
	
	//�ͷ���Դ
	public void closeConnection(){
		//�ͷŽ����
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
		//�ͷ�����
		if(conn!=null){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	
	}

	//��ɾ��
	public int executeUpdate(String sql,String []paras){
		int count=0;
		try {
			this.getConnection();//�������ݿ�
			pst=conn.prepareStatement(sql);//����sql��䴴��Ԥ�������
			//���paras��Ϊ�ջ��߳��ȴ���0 �͸�ֵ
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
	
	
	//��ѯ
	public ResultSet executeQuery(String sql,String[]paras){
		try {
			this.getConnection();//�������ݿ�
			pst=conn.prepareStatement(sql);//����sql��䴴��Ԥ�������
			//���paras��Ϊ�ջ��߳��ȴ���0 �͸�ֵ
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