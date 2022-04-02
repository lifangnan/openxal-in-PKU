package xal.app.mysql2accelerator;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.TextComponent;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import org.hamcrest.core.IsInstanceOf;

import xal.app.machinesimulator.MachineSimulationHistoryRecord;
import xal.app.machinesimulator.NodePropertyHistoryRecord;
import xal.app.machinesimulator.NodePropertyRecord;
import xal.app.rfsimulator.Accelerator;
import xal.ca.Channel;
import xal.ca.ConnectionException;
import xal.ca.PutException;
import xal.extension.bricks.WindowReference;
import xal.extension.widgets.swing.KeyValueFilteredTableModel;
import xal.extension.widgets.swing.KeyValueTableModel;
import xal.smf.AcceleratorNode;
import xal.smf.AcceleratorSeq;
import xal.smf.impl.Electromagnet;

public class Mysql2acceleratorController {
	
//	/** database table model */
//    final private KeyValueFilteredTableModel<> DATABASE_TABLE_MODEL;
//    /**accelerator node table model*/
//    final private KeyValueFilteredTableModel<> ACCELERATOR_TABLE_MODEL;
	
	/** main doucment */
	Mysql2acceleratorDocument mainDocument = null;
	
	/** mainwindow*/
	Window mainWindow;
	
	/** ����mysql�ĶԻ��� */
	SQL_dialog sql_dialog = null;
	ResultSet rSet = null;
	ResultSetMetaData sqlMetaData = null;
	
	/** related to SQL table */
//	final private KeyValueTableModel<NodeSqlRecord> SQL_TABLE_MODEL;
	SQLControllerModel sqlControllerModel = null;
	JTable Database_Table = null;
	Vector<String> DB_colnames = null;
	
	
	/** related to accelerator table */
	AcceleratorSeq selectedSeq = null;
	KeyValueFilteredTableModel<NodePropertyRecord> Accelerator_Table_Model;
	JTable Accelerator_Table = null;
	DefaultTableModel acceleratorTableModel = null;
	Vector<Vector<Object>> AcceleratorTableData = null;
	Vector<String> AcceleratorTableColumnNames = null;
	Vector<String> AcceleratorNodesId = null;
	final static int col_of_nodeID = 0;
	final static int col_of_checkbox = 6;
	final static int col_of_liveValue = 4;
	Vector<ChannelMonitor> camonitors = null;
	Vector<Channel> allChannels = null;
	
	
	/** Buttons*/
	JButton Refresh_Database_Button;
	JButton Send_Data_Button;
	JButton check_All_Button;
	JButton Uncheck_All_Button;
	
	public Connection connection = null;
   
	
	public Mysql2acceleratorController(final Mysql2acceleratorDocument document, final WindowReference windowReference) {
		mainDocument = document;
//		SQL_TABLE_MODEL = new KeyValueTableModel<NodeSqlRecord>();
		configureMainWindow(windowReference);
	}
	
	private void configureMainWindow( final WindowReference windowReference ) {
		mainWindow = windowReference.getWindow();
//		mainWindow.setSize(1800, 1200);
		
		configure_Link_to_MySQL_Button(windowReference);
		try {
			makeDatabaseView(windowReference);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		makeAcceleratorTableView(windowReference);
		ConfigureSendDataButton(windowReference);
		configureCheckAllButton(windowReference);
		configureUncheckAllButton(windowReference);
		
		
	}
	
	
	// ����mysql������
	private void makeDatabaseView( final WindowReference windowReference ) throws SQLException{
		Database_Table = (JTable) windowReference.getView("Database Table");
		Database_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sqlControllerModel = new SQLControllerModel(this);
		updateDatabaseView();		
		
		Refresh_Database_Button = (JButton) windowReference.getView("Refresh Database Button");
		Refresh_Database_Button.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDatabaseView();
			}
		} );
	}
	
	public void updateDatabaseView() {
		if(sqlControllerModel.isConnectionPrepared()) {
			if(sqlControllerModel == null) sqlControllerModel = new SQLControllerModel(this);
			DB_colnames = sqlControllerModel.getColumnNames();
			Vector<Vector> tableData = sqlControllerModel.getTableData();
			Database_Table.setModel( new DefaultTableModel( tableData, DB_colnames ) );
		}else {
			System.out.println("SQL connection may be unavailable��");
		}
	}
	
	
	
	private void ConfigureSendDataButton( final WindowReference windowReference ) {
		// related to: Database_Table, Accelerator_Table
		Send_Data_Button = (JButton) windowReference.getView("Send Data Button");
				
		Send_Data_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<String> accelerator_nodeIDs = AcceleratorNodesId;
				Vector<String> database_nodeIDs = DB_colnames;
				Vector<Channel> accTableChannels = allChannels;
				Vector<Boolean> checkBoxState = new Vector<Boolean>();
				for(int i = 0; i < Accelerator_Table.getRowCount(); i++) {
					checkBoxState.add((Boolean) Accelerator_Table.getValueAt(i, col_of_checkbox));
				}
				Vector<String> Selected_rowData = new Vector<String>(); 
				
				int selectedRow = Database_Table.getSelectedRow();
				if(selectedRow == -1) {
					System.out.println("No row is selected.");
				}else {
					for(int i = 0; i < Database_Table.getColumnCount(); i++) {
						Selected_rowData.add( String.valueOf(Database_Table.getValueAt(selectedRow, i)) );
					}
					
				}
				
				int DbTableColIndex = -1;
				double DB_value;
				Channel set_PV = null;
				if(selectedRow != -1 && accelerator_nodeIDs != null) {
					for(int AccTableIndex = 0; AccTableIndex < accelerator_nodeIDs.size(); AccTableIndex++) {
						// if node is selected
						if( checkBoxState.get(AccTableIndex) == true ) {
							DbTableColIndex = database_nodeIDs.indexOf( accelerator_nodeIDs.get(AccTableIndex) );
							if( DbTableColIndex == -1) {
								System.out.println("The selected node " + accelerator_nodeIDs.get(AccTableIndex) + "is not in the current database!");
							}else {
								if( Selected_rowData.get(DbTableColIndex) == null || Selected_rowData.get(DbTableColIndex) == "null") {
									System.out.println("The value of "+ accelerator_nodeIDs.get(AccTableIndex) + " in database is null or unvalid!");
								}else {
									DB_value = Double.parseDouble( Selected_rowData.get(DbTableColIndex) );
									set_PV = accTableChannels.get(AccTableIndex);
									try {
										set_PV.putVal(DB_value);
									} catch (ConnectionException | PutException e1) {
										e1.printStackTrace();
									}
								}
							}
						}
					}
				}
				
			}
		});
	}
	
	private void configureCheckAllButton( final WindowReference windowReference ) {
		check_All_Button = (JButton) windowReference.getView("Check All");
		check_All_Button.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowCount = Accelerator_Table.getRowCount();
				for(int i = 0; i < rowCount; i++) {
					Accelerator_Table.setValueAt(true, i, col_of_checkbox);
				}
			}
		} );
	}
	
	private void configureUncheckAllButton( final WindowReference windowReference ) {
		Uncheck_All_Button = (JButton) windowReference.getView("Uncheck All");
		Uncheck_All_Button.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int rowCount = Accelerator_Table.getRowCount();
				for(int i = 0; i < rowCount; i++) {
					Accelerator_Table.setValueAt(false, i, col_of_checkbox);
				}
			}
		} );
	}
	
	
	private void makeAcceleratorTableView( final WindowReference windowReference ) {
		Accelerator_Table = (JTable) windowReference.getView("Accelerator Table");
		
		Accelerator_Table.addMouseListener(new MouseListener() {		
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {	
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {	
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = Accelerator_Table.getSelectedRow();
				boolean checked = (boolean) Accelerator_Table.getValueAt(row, col_of_checkbox);
				Accelerator_Table.setValueAt(!checked, row, col_of_checkbox);
			}
		});
		
		AcceleratorTableColumnNames = new Vector<String>();
		AcceleratorTableColumnNames.add("Node ID");
		AcceleratorTableColumnNames.add("Device Type");
		AcceleratorTableColumnNames.add("Position");
//		AcceleratorTableColumnNames.add("Length");
		AcceleratorTableColumnNames.add("Value Type");
		AcceleratorTableColumnNames.add("Live Value");
		AcceleratorTableColumnNames.add("Set PV");
		AcceleratorTableColumnNames.add("Checked");
		
		updateAcceleratorTableView();
		
		
	}
	
	public void updateAcceleratorTableView() {
		AcceleratorSeq selectedSeq = mainDocument.getSelectedSequence();
		if(selectedSeq == null) {
			return;
		}
				
		AcceleratorNodesId = new Vector<String>();
		camonitors = new Vector<ChannelMonitor>();		
		allChannels = new Vector<Channel>();
		AcceleratorTableData = new Vector<Vector<Object>>();
		
		List<AcceleratorNode> allNodes = selectedSeq.getAllNodes();
		int row_count = 0;
		Electromagnet tempMagnetNode = null;
		for( AcceleratorNode node: allNodes ) {
			Vector<Object> oneRow = new Vector<Object>();
			if(node instanceof Electromagnet) {
				tempMagnetNode = (Electromagnet)node;
				oneRow.add(node.getId());
				AcceleratorNodesId.add( node.getId() );
				oneRow.add(tempMagnetNode.getType());
				oneRow.add(node.getPosition());
//				oneRow.add(node.getLength());
				oneRow.add("Current I");
				ChannelMonitor tempCAmonitor = new ChannelMonitor( tempMagnetNode.getMainSupply().getChannel("I") );
				tempCAmonitor.setRelatedCell(Accelerator_Table, row_count, col_of_liveValue);
				camonitors.add(tempCAmonitor);
				oneRow.add(null);
				oneRow.add( tempMagnetNode.getMainSupply().getChannel("I_Set").getId() );
				allChannels.add( tempMagnetNode.getMainSupply().getChannel("I_Set") );
				oneRow.add(true);
				AcceleratorTableData.add(oneRow);
				row_count++;
			}	
		}
		
		Accelerator_Table.setModel(new DefaultTableModel(AcceleratorTableData, AcceleratorTableColumnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
//				return super.isCellEditable(row, column);
			}
		});
		
		// cofigure checkbox column
		Accelerator_Table.getColumnModel().getColumn(col_of_checkbox).setCellRenderer(new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				JCheckBox ck = new JCheckBox();
//				ck.setSelected(isSelected);
				ck.setSelected((boolean) value);
				ck.setHorizontalAlignment( (int)0.5f );
				ck.setOpaque(false);
				return ck;
			}
		});
		
	}
	
	
	
	private void configure_Link_to_MySQL_Button(final WindowReference windowReference) {
		final JButton Link_to_MySQL_Button = (JButton) windowReference.getView( "Link to MySQL" );
		Link_to_MySQL_Button.addActionListener(event -> {
			if(sqlControllerModel == null) {
				sqlControllerModel = new SQLControllerModel(this);
			} else {
				sqlControllerModel.sql_dialog.setVisible(true);
			}
			
		});
		
	}
	
}

