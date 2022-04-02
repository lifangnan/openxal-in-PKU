package openxal;

import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.NumberFormat;

import xal.extension.widgets.swing.DecimalField;
import xal.smf.impl.WireHarp.DataRaw;
import xal.tools.data.DataAdaptor;
import xal.tools.xml.XmlDataAdaptor;
import xal.tools.xml.XmlDataAdaptor.ParseException;
import xal.tools.xml.XmlDataAdaptor.ResourceNotFoundException;

public class my_main {

	public static void main(String[] args) {
		try {
			XmlDataAdaptor documentAdaptor2 = XmlDataAdaptor.adaptorForFile(new File("D:\\2021\\pj1_openxal\\resources\\sns\\sns.xdxf"), false);
			DataAdaptor rootAdaptors1 = documentAdaptor2.childAdaptor("xdxf");
			List<DataAdaptor> list_seqAdaptors = rootAdaptors1.childAdaptors("sequence");
			for(DataAdaptor seqdp: list_seqAdaptors) {
//				System.out.println(seqdp.stringValue("id"));
				if(seqdp.stringValue("id").equals("DTL1")) {
					List<DataAdaptor> list_nodeAdaptors = seqdp.childAdaptors("node");
					for (DataAdaptor nodedp: list_nodeAdaptors) {
						System.out.println(nodedp.stringValue("id"));
					}
				}
			}
			
			
			
//			String[] attrs = rootAdaptors1.attributes();
//			for(String str: attrs) {
//				System.out.println(str);
//			}
			
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
		
		
		
//		XmlDataAdaptor documentAdaptor = XmlDataAdaptor.newEmptyDocumentAdaptor();
//		DataAdaptor sequenceAdaptor1 = documentAdaptor.createChild("sequence");
//		sequenceAdaptor1.setValue("id", "seq1");
//		DataAdaptor nodeAdaptor1 = sequenceAdaptor1.createChild("node");
//		nodeAdaptor1.setValue("node_id", "node1");
//		nodeAdaptor1.setValue("status", "enabled");
//		DataAdaptor nodeAdaptor2 = sequenceAdaptor1.createChild("node");
//		nodeAdaptor2.setValue("node_id", "node2");
//		nodeAdaptor2.setValue("status", "enabled");
		
//		System.out.println(nodeAdaptor2.attributes()[1]);
//		System.out.println(nodeAdaptor2.stringValue("status"));
		
//		try {
//			documentAdaptor.writeTo(new File("D:/2021/hello.xml"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	private void makeNoiseDialog() {
		JPanel settingPanel = new JPanel();
		JPanel noiseLevelPanel = new JPanel();
		JPanel offsetPanel = new JPanel();
		
		// for noise %
		noiseLevelPanel.setLayout(new GridLayout(7, 1));
		noiseLevelPanel.add(new JLabel("Noise Level for Device Type:"));
		
		JLabel percent = new JLabel("%");
		NumberFormat numberFormat;
		numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(3);
        
		JPanel noiseLevel1 = new JPanel();
		noiseLevel1.setLayout(new GridLayout(1, 3));
		JLabel label1 = new JLabel("Quad: ");
		DecimalField df1 = new DecimalField( 0., 5, numberFormat );
		noiseLevel1.add(label1);
		noiseLevel1.add(df1);
		noiseLevel1.add(percent);
		noiseLevelPanel.add(noiseLevel1);
        
		JPanel noiseLevel2 = new JPanel();
		noiseLevel2.setLayout(new GridLayout(1, 3));
		JLabel label2 = new JLabel("Bending Dipole: ");
		percent = new JLabel("%");
		DecimalField df2 = new DecimalField( 0., 5, numberFormat );
		noiseLevel2.add(label2);
		noiseLevel2.add(df2);
		noiseLevel2.add(percent);
		noiseLevelPanel.add(noiseLevel2);
		
		JPanel noiseLevel3 = new JPanel();
		noiseLevel3.setLayout(new GridLayout(1, 3));
		DecimalField df3 = new DecimalField( 0., 5, numberFormat );
		noiseLevel3.add(new JLabel("Dipole Corr.: "));
		noiseLevel3.add(df3);
		noiseLevel3.add(new JLabel("%"));
		noiseLevelPanel.add(noiseLevel3);
		
		JPanel noiseLevel5 = new JPanel();
		noiseLevel5.setLayout(new GridLayout(1, 3));
		DecimalField df5 = new DecimalField( 0., 5, numberFormat );
		noiseLevel5.add(new JLabel("Solenoid: "));
		noiseLevel5.add(df5);
		noiseLevel5.add(new JLabel("%"));
		noiseLevelPanel.add(noiseLevel5);
		
		JPanel noiseLevel4 = new JPanel();
		noiseLevel4.setLayout(new GridLayout(1, 3));
		DecimalField df4 = new DecimalField( 0., 5, numberFormat );
		noiseLevel4.add(new JLabel("BPM: "));
		noiseLevel4.add(df4);
		noiseLevel4.add(new JLabel("%"));
		noiseLevelPanel.add(noiseLevel4);
		
		// for offsets
		offsetPanel.setLayout(new GridLayout(7, 1));
		offsetPanel.add(new JLabel("Offset for Device Type:"));
		
		JPanel offset1 = new JPanel();
		offset1.setLayout(new GridLayout(1, 2));
		DecimalField df11 = new DecimalField( 0., 5, numberFormat );
		offset1.add(new JLabel("Quad: "));
		offset1.add(df11);
		offsetPanel.add(offset1);
		
		JPanel offset2 = new JPanel();
		offset2.setLayout(new GridLayout(1, 2));
		DecimalField df21 = new DecimalField( 0., 5, numberFormat );
		offset2.add(new JLabel("Bending Dipole: "));
		offset2.add(df21);
		offsetPanel.add(offset2);
		
		JPanel offset3 = new JPanel();
		offset3.setLayout(new GridLayout(1, 2));
		DecimalField df31 = new DecimalField( 0., 5, numberFormat );
		offset3.add(new JLabel("Dipole Corr.: "));
		offset3.add(df31);
		offsetPanel.add(offset3);
		
		JPanel offset5 = new JPanel();
		offset5.setLayout(new GridLayout(1, 2));
		DecimalField df51 = new DecimalField( 0., 5, numberFormat );
		offset5.add(new JLabel("Solenoid: "));
		offset5.add(df51);
		offsetPanel.add(offset5);
		
		JPanel offset4 = new JPanel();
		offset4.setLayout(new GridLayout(1, 2));
		DecimalField df41 = new DecimalField( 0., 5, numberFormat );
		offset4.add(new JLabel("BPM: "));
		offset4.add(df41);
		offsetPanel.add(offset4);
		
//		// put everything together
//		setNoise.setBounds(300, 300, 300, 300);
//		setNoise.setTitle("Set Noise Level...");
//		settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));
//		settingPanel.add(noiseLevelPanel);
//		settingPanel.add(offsetPanel);
//		setNoise.getContentPane().setLayout(new BorderLayout());
//		setNoise.getContentPane().add(settingPanel, BorderLayout.CENTER);
//		setNoise.getContentPane().add(done, BorderLayout.SOUTH);
//		done.setActionCommand("noiseSet");
//		done.addActionListener(this);
//		setNoise.pack();
	}

}
