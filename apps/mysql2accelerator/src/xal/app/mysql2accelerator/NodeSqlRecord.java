package xal.app.mysql2accelerator;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Fangnan Li
 * record the values used for SQL
 */
public class NodeSqlRecord {
	/**accelerator node id*/
	final private String NODE_ID;
	/**the list of values to show*/
	final private Map<Integer, Double> VALUES_SHOW;
	
	/**Constructor*/
	public NodeSqlRecord( final String nodeId){
		NODE_ID = nodeId;	
		VALUES_SHOW = new TreeMap<Integer,Double>();
	}
	
	/**get the node id*/
	public String getNodeId(){
		return NODE_ID;
	}
	

	/**get the values used for simulations*/
	public Double[] getValues(){
		Double[] values = new Double[VALUES_SHOW.size()];
		VALUES_SHOW.values().toArray( values );
		
		return values;
	}
	
	/**add a value to the value list*/
	public void addValue( final int index, final double value ){
		VALUES_SHOW.put( index, value );
	}
	
	/**remove the value with specified time key*/
	public void removeValue( final int index ){
		VALUES_SHOW.remove( index );
	}
	

}