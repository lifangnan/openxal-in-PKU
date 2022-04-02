package xal.app.mysql2accelerator;

import java.awt.Dimension;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import xal.ca.Channel;
import xal.ca.ChannelFactory;
import xal.ca.ConnectionException;
import xal.ca.GetException;
import xal.smf.Accelerator;
import xal.smf.data.XMLDataManager;
import xal.smf.impl.Electromagnet;

public class test_main {

	public static void main(String[] args) throws ConnectionException, GetException, InterruptedException {
		// TODO Auto-generated method stub
//		SQLControllerModel sqlcontroller = new SQLControllerModel(null);
//		
////		Vector<String> columnnames = sqlcontroller.getColumnNames();
//		sqlcontroller.getTableData();
		
//		ChannelFactory caFac = ChannelFactory.defaultFactory();
//		Channel ca = caFac.getChannel("counter");
//		System.out.println(ca.getValInt());
//		
//		ChannelMonitor camonitor = new ChannelMonitor(ca);
//		Vector<String> string_vec = new Vector<String>(); 
		
//		for(int i = 0; i<100; i++) {
////			Thread.sleep(500);
//////			System.out.println( camonitor.getLatestValue() );
//			String temp = new String(" ");
//			temp = temp + i;
//			string_vec.add(temp);
//		}
//		
//		for(String item : string_vec) {
//			System.out.print(item);
//		}
//		
//		Accelerator acc = XMLDataManager.acceleratorWithPath("I:\\备份文件_李方楠\\clapa2_model\\main.xal");
//		Electromagnet sol = (Electromagnet) acc.getNode("SOLENOID1");
//		Channel ca = sol.getMainSupply().getChannel("I");
//		System.out.println(ca.getId());
		
//		ChannelFactory caFac = ChannelFactory.defaultFactory();
//		Channel ca2 = caFac.getChannel("IT:PSQ1:GetCurrent");
//		ChannelMonitor caMonitor = new ChannelMonitor(ca);
		
//		System.out.println(sol.getType());
//		System.out.println(sol.getPId());
//		System.out.println(sol.getClass());
//		
//		JFrame mainwindows = new JFrame();
//		mainwindows.setSize(new Dimension(400,400));
//		
//		Vector<String> colname = new Vector<String>();
//		colname.add("A");
//		colname.add("B");
//		
//		Vector<Vector<Integer>> data = new Vector<Vector<Integer>>();
//		Vector<Integer> row1 = new Vector<Integer>();
//		row1.add(1); row1.add(2);
//		Vector<Integer> row2 = new Vector<Integer>();
//		row2.add(3); row2.add(4);
//		data.add(row1);
//		data.add(row2);
//		
//		DefaultTableModel tableModel = new DefaultTableModel(data, colname);
//		JTable table = new JTable(tableModel);
//		
//		mainwindows.getContentPane().add(table);
//		mainwindows.setVisible(true);
		
//		tableModel.setValueAt(10, 0, 0);
//		table.setValueAt(20, 0, 0);
		
//		for(int i = 0; i<100; i++) {
//			Thread.sleep(500);
//			System.out.println( caMonitor.getLatestValue() );
//		}
//		System.out.println( table.getColumnCount() );
		boolean a = true;
		Boolean test_bool = (Boolean) a;
		if( test_bool == true ) {
			System.out.println(a);
		}
	}

}
