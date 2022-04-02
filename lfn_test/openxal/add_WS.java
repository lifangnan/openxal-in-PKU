package openxal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import xal.tools.data.DataAdaptor;
import xal.tools.xml.XmlDataAdaptor;
import xal.tools.xml.XmlDataAdaptor.ParseException;
import xal.tools.xml.XmlDataAdaptor.ResourceNotFoundException;

public class add_WS {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			XmlDataAdaptor documentAdaptor2 = XmlDataAdaptor.adaptorForFile(new File("D:/2021/clapa1_model_Permanent_quad/clapa.xdxf"), false);
			DataAdaptor rootAdaptors1 = documentAdaptor2.childAdaptor("xdxf");
			List<DataAdaptor> list_seqAdaptors = rootAdaptors1.childAdaptors("sequence");
			
			int number = 1;
			for(DataAdaptor seqdp: list_seqAdaptors) {
//				System.out.println(seqdp.stringValue("len"));
				String seq_id = seqdp.stringValue("id");
//				System.out.println(seq_id.equals("QuadTrip"));
				if(!seq_id.equals("QuadTrip")) {continue;}
				System.out.println(seq_id);
				float length = Float.parseFloat(seqdp.stringValue("len"));
				
				
				for(float i=0; i<length; i+=0.1) {
//					if(i > 1.93025-0.9305/2 && i < 1.93025+0.9305/2) {
//						continue;
//					}
					
					String ws_id = seq_id+":WS"+number;
					DataAdaptor node_Adaptor = seqdp.createChild("node");
					node_Adaptor.setValue("id", ws_id);
					number++;
					node_Adaptor.setValue("len","0.0");
					node_Adaptor.setValue("pos", Float.toString((float)Math.round(i*1000)/1000 ) );
					node_Adaptor.setValue("softType","Version 1.0.0");
					node_Adaptor.setValue("status","true");
					node_Adaptor.setValue("type","WS");
					
					DataAdaptor attri_Adaptor = node_Adaptor.createChild("attributes");
					DataAdaptor attri_align_Adaptor = attri_Adaptor.createChild("align");
					if(i < 1.93025) {
						attri_align_Adaptor.setValue("pitch", "0");
					}else {
						attri_align_Adaptor.setValue("pitch", "-90");
					}
					attri_align_Adaptor.setValue("roll", "0");
					attri_align_Adaptor.setValue("yaw", "0");
					attri_align_Adaptor.setValue("x", "0");
					attri_align_Adaptor.setValue("y", "0");
					attri_align_Adaptor.setValue("z", "0");

					DataAdaptor channelsuite_Adaptor = 	node_Adaptor.createChild("channelsuite");
					channelsuite_Adaptor.setValue("name", "wssuite");
					add_channel(channelsuite_Adaptor, "position", "false", ws_id+":Pos");
					add_channel(channelsuite_Adaptor, "statusArray", "false", ws_id+":statusArray");
					add_channel(channelsuite_Adaptor, "abortScan", "true", ws_id+":AbortScan");
					add_channel(channelsuite_Adaptor, "vDataArray", "false", ws_id+":XRaw");
					add_channel(channelsuite_Adaptor, "dDataArray", "false", ws_id+":YRaw");
					add_channel(channelsuite_Adaptor, "hDataArray", "false", ws_id+":ZRaw");
					add_channel(channelsuite_Adaptor, "positionArray", "false", ws_id+":PosArray");
					add_channel(channelsuite_Adaptor, "beginScan", "true", ws_id+":BeginScan");
					add_channel(channelsuite_Adaptor, "scanLength", "false", ws_id+":ScanLen");
					add_channel(channelsuite_Adaptor, "vSigmaF", "false", ws_id+":XSigmaF");
					add_channel(channelsuite_Adaptor, "dSigmaF", "false", ws_id+":YSigmaF");
					add_channel(channelsuite_Adaptor, "hSigmaF", "false", ws_id+":ZSigmaF");
					add_channel(channelsuite_Adaptor, "vFit", "false", ws_id+":XFit");
					add_channel(channelsuite_Adaptor, "dFit", "false", ws_id+":YFit");
					add_channel(channelsuite_Adaptor, "hFit", "false", ws_id+":ZFit");
					add_channel(channelsuite_Adaptor, "vPos", "false", ws_id+":XPos");
					add_channel(channelsuite_Adaptor, "dPos", "false", ws_id+":YPos");
					add_channel(channelsuite_Adaptor, "hPos", "false", ws_id+":ZPos");
					add_channel(channelsuite_Adaptor, "vSigmaM", "false", ws_id+":XSigmaM");
					add_channel(channelsuite_Adaptor, "dSigmaM", "false", ws_id+":YSigmaM");
					add_channel(channelsuite_Adaptor, "hSigmaM", "false", ws_id+":ZSigmaM");
					add_channel(channelsuite_Adaptor, "vAreaF", "false", ws_id+":XAreaF");
					add_channel(channelsuite_Adaptor, "dAreaF", "false", ws_id+":YAreaF");
					add_channel(channelsuite_Adaptor, "hAreaF", "false", ws_id+":ZAreaF");
					add_channel(channelsuite_Adaptor, "vAreaM", "false", ws_id+":XAreaM");
					add_channel(channelsuite_Adaptor, "dAreaM", "false", ws_id+":YAreaM");
					add_channel(channelsuite_Adaptor, "hAreaM", "false", ws_id+":ZAreaM");
					add_channel(channelsuite_Adaptor, "vAmpF", "false", ws_id+":XAmplF");
					add_channel(channelsuite_Adaptor, "dAmpF", "false", ws_id+":YAmplF");
					add_channel(channelsuite_Adaptor, "hAmpF", "false", ws_id+":ZAmplF");
					add_channel(channelsuite_Adaptor, "vAmpM", "false", ws_id+":XAmplM");
					add_channel(channelsuite_Adaptor, "dAmpM", "false", ws_id+":YAmplM");
					add_channel(channelsuite_Adaptor, "hAmpM", "false", ws_id+":ZAmplM");
					add_channel(channelsuite_Adaptor, "vMeanF", "false", ws_id+":XMeanF");
					add_channel(channelsuite_Adaptor, "dMeanF", "false", ws_id+":YMeanF");
					add_channel(channelsuite_Adaptor, "hMeanF", "false", ws_id+":ZMeanF");
					add_channel(channelsuite_Adaptor, "vMeanM", "false", ws_id+":XMeanM");
					add_channel(channelsuite_Adaptor, "dMeanM", "false", ws_id+":YMeanM");
					add_channel(channelsuite_Adaptor, "hMeanM", "false", ws_id+":ZMeanM");
					 add_channel(channelsuite_Adaptor, "vOffstF", "false", ws_id+":XOffstF");
                     add_channel(channelsuite_Adaptor, "dOffstF", "false", ws_id+":YOffstF");
                     add_channel(channelsuite_Adaptor, "hOffstF", "false", ws_id+":ZOffstF");
                     add_channel(channelsuite_Adaptor, "vOffstM", "false", ws_id+":XOffstM");
                     add_channel(channelsuite_Adaptor, "dOffstM", "false", ws_id+":YOffstM");
                     add_channel(channelsuite_Adaptor, "hOffstM", "false", ws_id+":ZOffstM");
                     add_channel(channelsuite_Adaptor, "vSlopeF", "false", ws_id+":XSlopeF");
                     add_channel(channelsuite_Adaptor, "dSlopeF", "false", ws_id+":YSlopeF");
                     add_channel(channelsuite_Adaptor, "hSlopeF", "false", ws_id+":ZSlopeF");
                     add_channel(channelsuite_Adaptor, "vSlopeM", "false", ws_id+":XSlopeM");
                     add_channel(channelsuite_Adaptor, "dSlopeM", "false", ws_id+":YSlopeM");
                     add_channel(channelsuite_Adaptor, "hSlopeM", "false", ws_id+":ZSlopeM");
					add_channel(channelsuite_Adaptor, "nSteps", "true", ws_id+":Steps");
					add_channel(channelsuite_Adaptor, "hRealData", "false", ws_id+":HorzSamp");
					add_channel(channelsuite_Adaptor, "vRealData", "false", ws_id+":VertSamp");
					add_channel(channelsuite_Adaptor, "dRealData", "false", ws_id+":DiagSamp");
				}
				
			}
			
			try {
				documentAdaptor2.writeTo(new File("D:/2021/clapa1_model_Permanent_quad/clapa.xdxf"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Accelerator theAccelerator = XMLDataManager.acceleratorWithPath("D:\\2021\\pj1_openxal\\resources\\sns\\main.xal", ChannelFactory.defaultFactory() );
//			setAccelerator( theAccelerator, "D:\\2021\\pj1_openxal\\resources\\sns\\main.xal" );
			
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

	public static void add_channel(final DataAdaptor channelsuiteAdaptor, String handle, String settable, String signal) {
		DataAdaptor channelAdaptor = channelsuiteAdaptor.createChild("channel");
		channelAdaptor.setValue("handle", handle);
		channelAdaptor.setValue("settable", settable);
		channelAdaptor.setValue("signal", signal);
	}
}
