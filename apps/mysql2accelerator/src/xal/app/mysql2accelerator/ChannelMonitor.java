package xal.app.mysql2accelerator;
/**
 * 
 */


import java.text.NumberFormat;

import javax.swing.JTable;

import xal.ca.Channel;
import xal.ca.ChannelRecord;
import xal.ca.ConnectionException;
import xal.ca.ConnectionListener;
import xal.ca.IEventSinkValue;
import xal.ca.Monitor;
import xal.ca.MonitorException;

/**
 * @author luxiaohan
 *The channel monitor to monitor the value changes of the channel
 */
public class ChannelMonitor implements IEventSinkValue, ConnectionListener {
	
	/** The channel object */
	private Channel channel;
	/** the latest live value */
	private volatile double latestValue = Double.NaN;
	/** the monitor for this channel */
	private Monitor monitor;
	
	/** The channal monitor will update a live value in this table */
	private JTable liveValueTable = null;
	private int row;
	private int col;
	
	
	NumberFormat nf = null;
	
	/** the constructor */
	public ChannelMonitor( Channel channel ) { 
		this.channel = channel;
		this.channel.addConnectionListener(this);
		this.channel.requestConnection();
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(5);
	}
	
	/** returns the latest value from this Channel */
	protected double getLatestValue() { 
		return latestValue;
	}
	
	/** make a monitor connection to a channel */
	private void makeMonitor() {
		try {
		    monitor = channel.addMonitorValue(this, Monitor.VALUE);	
		}
		catch(ConnectionException exc) {}
		catch(MonitorException exc) {}
	}

	/** The Connection Listener interface */
	public void connectionMade(Channel chan) {		
		if (monitor == null) makeMonitor();
	}
	
	/** ConnectionListener interface */
	public void connectionDropped(Channel aChannel) {
		latestValue = Double.NaN;
	}


	/** interface method for IEventSinkVal */
	public void eventValue(ChannelRecord newRecord, Channel chan) {
		latestValue = newRecord.doubleValue();
//		System.out.println(latestValue);
		
		if(liveValueTable != null && liveValueTable.getRowCount() > row) {
			liveValueTable.setValueAt( nf.format(latestValue), row, col );
		}
	}
	
	
	/** set related table and which cell to update */
	public void setRelatedCell(JTable table, int row_index, int col_index) {
		liveValueTable = table;
		row = row_index;
		col = col_index;
	}
}
