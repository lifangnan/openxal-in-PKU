package connect_mySql.databaseUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBUtil dbUtil = DBUtil.getDBUtil();
		dbUtil.exeute("create table if not exists admin(id int primary key," +
				"name varchar(32)," +
				"username varchar(32)," +
				"password varchar(32))");
		dbUtil.exeute("insert into admin(id, name, username, password) values(1, 'jwc', 'jwc', 'jwc')");
		
		String username = "jwc";
		String password = "jwc";
		String sql = "select * from admin where username=? and password=?";
		String[] param = { username, password };
		ResultSet rs = dbUtil.executeQuery(sql, param);
		
		boolean result = false; //�Ƿ���mysql�в�ѯ���˸��û���������
		try {
			if (rs.next()) {
				result = true; // ��ѯ���ˣ�˵�����Ե�½
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(result) {
			System.out.println("�ɹ���½");
		}else {
			System.out.println("��½ʧ��");
		}
	}

}
