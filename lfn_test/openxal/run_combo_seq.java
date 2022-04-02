package openxal;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import xal.ca.ConnectionException;
import xal.ca.GetException;
import xal.ca.PutException;
import xal.model.IElement;
import xal.model.ModelException;
import xal.model.alg.EnvTrackerAdapt;
import xal.model.elem.IdealMagDipoleFace2;
import xal.model.elem.IdealMagFringeQuad;
import xal.model.elem.IdealMagSectorDipole2;
import xal.model.probe.EnvelopeProbe;
import xal.model.probe.traj.EnvelopeProbeState;
import xal.model.probe.traj.Trajectory;
import xal.sim.scenario.AlgorithmFactory;
import xal.sim.scenario.ProbeFactory;
import xal.sim.scenario.Scenario;
import xal.smf.Accelerator;
import xal.smf.AcceleratorSeq;
import xal.smf.AcceleratorSeqCombo;
import xal.smf.AcceleratorNode;
import xal.smf.data.XMLDataManager;
import xal.smf.impl.Bend;
import xal.smf.impl.Magnet;
import xal.smf.impl.Quadrupole;
import xal.tools.URLUtil;
import xal.tools.beam.CovarianceMatrix;
import xal.tools.beam.PhaseMap;
import xal.tools.beam.Twiss;

public class run_combo_seq extends Application{
	
	static ArrayList<Double> position = new ArrayList<>();
	static ArrayList<Double> sigmaX = new ArrayList<>();
	static ArrayList<Double> sigmaY = new ArrayList<>();
	static ArrayList<Double> meanX = new ArrayList<>();
	static ArrayList<Double> meanY = new ArrayList<>();
	static Double max_posi, min_y, max_y;
	
	public static void main(String[] args) throws MalformedURLException, InstantiationException, ModelException, ConnectionException, GetException, PutException {
//		1. 加载加速器配置
		Accelerator clapa1 = XMLDataManager.acceleratorWithPath("D:\\2021\\clapa1_model_Permanent_quad\\main.xal");
//		List<AcceleratorSeq> tri_quad = clapa1.getSequences();
//		for(AcceleratorSeq seq: tri_quad) {
//			System.out.println(seq.getId());
//		}
		
//		String urlSpec = URLUtil.urlSpecForFilePath( "D:\\2021\\xml_writer_test\\main.xal" );
//		XMLDataManager test_xmlManager = new XMLDataManager( urlSpec );
		
		
//		String seqname = "Bending";
//		String seqname = "QuadTrip";
//		AcceleratorSeq Acc_Seq = clapa1.getSequence(seqname);
		
//		2. 选择加速器中需要模拟的一段
//		AcceleratorSeqCombo Acc_Seq = clapa1.getComboSequence("Quad+Bending");
		AcceleratorSeqCombo Acc_Seq = clapa1.getComboSequence("CLAPA1");
//		AcceleratorSeq Acc_Seq = clapa1.getSequence(seqname);
		
//		List<AcceleratorNode> list_node = Acc_Seq.getNodes();
		
//		3. 获取加速器中特定节点（偏转磁铁），然后修改其磁场数值
//		Bend bendmag = (Bend) Acc_Seq.getNodeWithId("Bending_Mag:DH01");
		
//		double B = 0;
//		for(int i=0; i<200; ++i) {
//			B = 0.4783124686843 + i*0.000000000000001;
//			bendmag.setDfltField(B);
//			System.out.println(B);
//			Double[] ans = do_simulation(Acc_Seq);
//			if(ans[2] < 0) {
//				break;
//			}
//		}
		
//		
//		Quadrupole QH01 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QH01");
//		Quadrupole QV02 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QV02");
//		Quadrupole QH03 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QH03");
//		Quadrupole QV04 = (Quadrupole) Acc_Seq.getNodeWithId("QuadDoub_Mag:QV04");
//		Quadrupole QH05 = (Quadrupole) Acc_Seq.getNodeWithId("QuadDoub_Mag:QH05");
		
//		Double IntK0 = -0.007228819457046502;
//		QH01.setFringeFieldIntegralK0(IntK0);
//		QV02.setFringeFieldIntegralK0(-IntK0);
//		QH03.setFringeFieldIntegralK0(IntK0);
		
//		QH01.setDfltField(17.271996817432928);
//		QV02.setDfltField(-10.690703847217987);
//		QH03.setDfltField(11.558160193097905);
		
//		QV04.setDfltField(-0.24030490443792818);
//		QH05.setDfltField(2.632628713688273);		
		
		Double[] ans = do_simulation(Acc_Seq);
		launch(args);	
	}
	
	public static Double[] do_simulation(AcceleratorSeq Acc_Seq) throws InstantiationException, ModelException {
//		清空记录
		position.clear();
		sigmaX.clear();
		sigmaY.clear();
		
		//		4. 设置模拟算法
		EnvTrackerAdapt etraker = AlgorithmFactory.createEnvTrackerAdapt(Acc_Seq);
		etraker.setMaxIterations(1000);
		etraker.setAccuracyOrder(1);
		etraker.setErrorTolerance(0.001);
		
//		5. 设置粒子数相关属性
		EnvelopeProbe probe = ProbeFactory.getEnvelopeProbe(Acc_Seq, etraker);
		probe.setBeamCurrent(8.01e-7);
		probe.setKineticEnergy(4.622e6);
		probe.setSpeciesCharge(1);
		probe.setSpeciesRestEnergy(939.29e6);
		probe.setBunchFrequency(5e8);		
		
////		6. 设置模拟起始节点
//		String LocStart = "Begin_Of_"+seqname;
//		AcceleratorNode nodeStart = Acc_Seq.getNodeWithId(LocStart);
//		double Posi_nodeStart = Acc_Seq.getPosition(nodeStart);
		
//		7. 创建模拟模型（Scenario）
		Scenario model = Scenario.newScenarioFor(Acc_Seq);
		model.setProbe(probe);
		model.setSynchronizationMode(Scenario.SYNC_MODE_DESIGN);
//		model.setStartNode(LocStart);
		
//		Bend bendmag = (Bend) Acc_Seq.getNodeWithId("Bending_Mag:DH01");
//		List<IElement> bend_model = model.elementsMappedTo(bendmag);
		
//		IdealMagSectorDipole2 sector = (IdealMagSectorDipole2) bend_model.get(1);
//		IdealMagDipoleFace2 entFace = (IdealMagDipoleFace2) bend_model.get(0);
//		IdealMagDipoleFace2 extFace = (IdealMagDipoleFace2) bend_model.get(2);
//		PhaseMap map = sector.transferMap(probe, 1);
//		PhaseMap map2 = entFace.transferMap(probe, 1);
//		PhaseMap map3 = extFace.transferMap(probe, 1);
		
		
//		8. 执行模拟
		model.run();
		
//		9. 获取轨迹信息，包括每个感兴趣位置的Twiss参数
		EnvelopeProbe probe2 = (EnvelopeProbe) model.getProbe();
		Trajectory<EnvelopeProbeState> traj = probe2.getTrajectory();
		
		EnvelopeProbeState dataFinal = traj.finalState();
		CovarianceMatrix cov_final =  dataFinal.getCovarianceMatrix();
		
		CovarianceMatrix cov_state =  null;
		for(EnvelopeProbeState state: traj){
			cov_state = state.getCovarianceMatrix();
//			System.out.println(state.getElementId());
			
			position.add(state.getPosition());
			sigmaX.add(cov_state.getSigmaX());
			sigmaY.add(cov_state.getSigmaY());
			meanX.add(cov_state.getMeanX());
			meanY.add(cov_state.getMeanY());
//			System.out.println(state.getPosition());
//			System.out.printf("sigmaX=%f, sigmaY=%f, sigmaZ=%f \n", cov_state.getSigmaX(), cov_state.getSigmaY(), cov_state.getSigmaZ());
		}
//		System.out.printf("meanX=%f, meanY=%f, meanZ=%f \n", cov_final.getMeanX(), cov_final.getMeanY(), cov_final.getMeanZ());
//		System.out.printf("sigmaX=%f, sigmaY=%f, sigmaZ=%f \n", cov_final.getSigmaX(), cov_final.getSigmaY(), cov_final.getSigmaZ());
		max_posi = Collections.max(position);
		ArrayList<Double> all_sigma = new ArrayList<>();
		all_sigma.addAll(sigmaX);
		all_sigma.addAll(sigmaY);
		min_y = Collections.min(all_sigma);
		max_y = Collections.max(all_sigma);
		
		double last_sigmaX = sigmaX.get(sigmaX.size()-1);
		double last_sigmaY = sigmaY.get(sigmaY.size()-1);
		double last_meanX = meanX.get(meanX.size()-1);
		double last_meanY = meanY.get(meanY.size()-1);
//		System.out.println("simgaX="+ last_sigmaX + "  sigmaY=" + last_sigmaY);
		System.out.println("meanX="+ last_meanX + "  meanY=" + last_meanY);
		
		Double[] last_sigma = {last_sigmaX, last_sigmaY, last_meanX, last_meanY};
		return last_sigma;
	}
	
	
	@Override
	public void start(Stage stage) {
		LineChart<Number, Number> line1 = getView1();
		line1.setPrefHeight(400);
		line1.setPrefWidth(1500);

		Group root = new Group();
		root.getChildren().add(line1);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("plotstyle.css").toExternalForm());
		
		stage.setScene(scene);
		stage.setTitle("First JavaFX");
		stage.setHeight(500);
		stage.setWidth(1600);
		stage.show();
	}
	
	public LineChart<Number, Number> getView1() {
		NumberAxis x = new NumberAxis("Position(m)", 0, max_posi, 0.5);
		NumberAxis y = new NumberAxis("Sigma(m)", min_y, max_y, 0.01);
		
		LineChart<Number, Number> linechart = new LineChart<Number, Number>(x, y);
		
		XYChart.Series<Number, Number> posi_sigmax = new XYChart.Series<Number, Number>();
		posi_sigmax.setName("posi_sigmax");
		
		XYChart.Series<Number, Number> posi_sigmay = new XYChart.Series<Number, Number>();
		posi_sigmay.setName("posi_sigmay");
		
		for(int i=0; i<position.size(); i++) {
			posi_sigmax.getData().add( new XYChart.Data<Number, Number>(position.get(i), sigmaX.get(i)) );
			posi_sigmay.getData().add( new XYChart.Data<Number, Number>(position.get(i), sigmaY.get(i)) );
		}
		
		linechart.getData().add(posi_sigmax);
		linechart.getData().add(posi_sigmay);
		
		return linechart;
	}
}
