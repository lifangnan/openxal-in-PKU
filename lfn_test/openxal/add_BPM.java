package openxal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.sun.scenario.effect.AbstractShadow;

import xal.ca.ChannelFactory;
import xal.extension.application.smf.AcceleratorDocument;
import xal.smf.Accelerator;
import xal.smf.data.XMLDataManager;
import xal.tools.data.DataAdaptor;
import xal.tools.xml.XmlDataAdaptor;
import xal.tools.xml.XmlDataAdaptor.ParseException;
import xal.tools.xml.XmlDataAdaptor.ResourceNotFoundException;

public class add_BPM extends AcceleratorDocument{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			XmlDataAdaptor documentAdaptor2 = XmlDataAdaptor.adaptorForFile(new File("D:/2021/clapa1_model/clapa.xdxf"), false);
			DataAdaptor rootAdaptors1 = documentAdaptor2.childAdaptor("xdxf");
			List<DataAdaptor> list_seqAdaptors = rootAdaptors1.childAdaptors("sequence");
//			
			
//			Accelerator theAccelerator = XMLDataManager.acceleratorWithPath("D:\\2021\\pj1_openxal\\resources\\sns\\main.xal", ChannelFactory.defaultFactory() );
//			setAccelerator( theAccelerator, "D:\\2021\\pj1_openxal\\resources\\sns\\main.xal" );
			
			for(DataAdaptor seqdp: list_seqAdaptors) {
//				System.out.println(seqdp.stringValue("id"));
				if(seqdp.stringValue("id").equals("Bending")) {
//					List<DataAdaptor> list_nodeAdaptors = seqdp.childAdaptors("node");
//					for (DataAdaptor nodedp: list_nodeAdaptors) {
//						System.out.println(nodedp.stringValue("id"));
//					}
					
					BPM_default_set(seqdp, "Bending-BPM01", "2.000", "0.010");
					BPM_default_set(seqdp, "Bending-BPM02", "2.500", "0.020");
					BPM_default_set(seqdp, "Bending-BPM03", "3.000", "0.030");
					BPM_default_set(seqdp, "Bending-BPM04", "3.500", "0.030");
					System.out.println("success");
					
					try {
						documentAdaptor2.writeTo(new File("D:/2021/clapa1_model/clapa.xdxf"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ResourceNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void makeMainWindow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveDocumentAs(URL url) {
		// TODO Auto-generated method stub
		
	}
		
	public static void BPM_default_set(final DataAdaptor seq_Adaptor, String id, String position, String length) {
		DataAdaptor node_Adaptor = seq_Adaptor.createChild("node");
		node_Adaptor.setValue("type", "BPM");
		node_Adaptor.setValue("id", id);
		node_Adaptor.setValue("pos", position);
		node_Adaptor.setValue("len", length);
		node_Adaptor.setValue("status", "true");
		DataAdaptor BPM01_attributes = node_Adaptor.createChild("attributes");
		DataAdaptor BPM01_attributes_align = BPM01_attributes.createChild("align");
		BPM01_attributes_align.setValue("x", "0.0");
		BPM01_attributes_align.setValue("y", "0.0");
		BPM01_attributes_align.setValue("z", "0.0");
		BPM01_attributes_align.setValue("pitch", "0");
		BPM01_attributes_align.setValue("yaw", "0");
		BPM01_attributes_align.setValue("roll", "0");
		DataAdaptor BPM01_attributes_bpm = BPM01_attributes.createChild("bpm");
		BPM01_attributes_bpm.setValue("frequency", "805.0");
		BPM01_attributes_bpm.setValue("length", "0.075");
		BPM01_attributes_bpm.setValue("orientation", "-1");
		
		DataAdaptor BPM01_channelsuite = node_Adaptor.createChild("channelsuite");
		BPM01_channelsuite.setValue("name", "bpmsuite");
		DataAdaptor channel1 = create_channel(BPM01_channelsuite, "xAvg", id+":xAvg");
		DataAdaptor channel2 = create_channel(BPM01_channelsuite, "yAvg", id+":yAvg");
		DataAdaptor channel3 = create_channel(BPM01_channelsuite, "xTBT", id+":xTBT");
		DataAdaptor channel4 = create_channel(BPM01_channelsuite, "yTBT", id+":yTBT");
		DataAdaptor channel5 = create_channel(BPM01_channelsuite, "phaseAvg", id+":phaseAvg");
		DataAdaptor channel6 = create_channel(BPM01_channelsuite, "amplitudeAvg", id+":amplitudeAvg");
		DataAdaptor channel7 = create_channel(BPM01_channelsuite, "ampTBT", id+":ampTBT");
		DataAdaptor channel8 = create_channel(BPM01_channelsuite, "phaseTBT", id+":phaseTBT");
		
	}
	
	public static DataAdaptor create_channel(DataAdaptor dp, String handle, String settable, String signal) {
		DataAdaptor channelAdaptor = dp.createChild("channel");
		channelAdaptor.setValue("handle", handle);
		channelAdaptor.setValue("settale", settable);
		channelAdaptor.setValue("signal", signal);
		
		return channelAdaptor;
	}
	
	public static DataAdaptor create_channel(DataAdaptor dp, String handle, String signal) {
		return create_channel(dp, handle, "false", signal);
	}
	
}
