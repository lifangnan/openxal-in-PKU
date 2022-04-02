package openxal;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.xml.internal.ws.dump.LoggingDumpTube.Position;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import xal.ca.ConnectionException;
import xal.ca.GetException;
import xal.model.ModelException;
import xal.model.alg.EnvTrackerAdapt;
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
import xal.smf.impl.Quadrupole;
import xal.tools.beam.CovarianceMatrix;

public class try_scenario extends Application{
	
	static ArrayList<Double> position = new ArrayList<>();
	static ArrayList<Double> sigmaX = new ArrayList<>();
	static ArrayList<Double> sigmaY = new ArrayList<>();
	static Double max_posi, min_y, max_y;
	
	public static void main(String[] args) throws MalformedURLException, InstantiationException, ModelException, ConnectionException, GetException {
		Accelerator clapa1 = XMLDataManager.acceleratorWithPath("D:\\2021\\clapa1_model_Permanent_quad\\main.xal");
		List<AcceleratorSeq> tri_quad = clapa1.getSequences();
		for(AcceleratorSeq seq: tri_quad) {
			System.out.println(seq.getId());
		}
		
		String seqname = "QuadTrip";
		AcceleratorSeq Acc_Seq = clapa1.getSequence(seqname);
		
//		Bend bendmag = (Bend) Acc_Seq.getNodeWithId("Bending_Mag:DH01");
//		bendmag.setDfltField(1.0);
		
		EnvTrackerAdapt etraker = AlgorithmFactory.createEnvTrackerAdapt(Acc_Seq);
		etraker.setMaxIterations(1000);
		etraker.setAccuracyOrder(1);
		etraker.setErrorTolerance(0.001);
		
		EnvelopeProbe probe = ProbeFactory.getEnvelopeProbe(Acc_Seq, etraker);
		probe.setBeamCurrent(0.038);
		probe.setKineticEnergy(885.e6);
		probe.setSpeciesCharge(1);
		probe.setSpeciesRestEnergy(939.29e6);
		
//		List<AcceleratorNode> all_nodes = Acc_Seq.getAllNodes();
//		for(AcceleratorNode node : all_nodes) {
//			System.out.println(node.getId());
//		}
		
		String LocStart = "Begin_Of_"+seqname;
		AcceleratorNode nodeStart = Acc_Seq.getNodeWithId(LocStart);
		double Posi_nodeStart = Acc_Seq.getPosition(nodeStart);
		
		Scenario model = Scenario.newScenarioFor(Acc_Seq);
		model.setProbe(probe);
		model.setSynchronizationMode(Scenario.SYNC_MODE_DESIGN);
		model.setStartNode(LocStart);
		
//		三元四极磁铁段设置
//		Quadrupole quad1 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QH01");
//		Quadrupole quad2 = (Quadrupole)Acc_Seq.getNodeWithId("QuadTrip_Mag:QV02");
//		model.setModelInput(quad1, "setField", -10.1);
		
//		偏转磁铁段设置
		Bend bend_mag = (Bend) Acc_Seq.getNodeWithId("Bending_Mag:DH01");
//		bend_mag.setDfltField(-2.0);
//		model.setModelInput(bend_mag, "setDfltField", -10.0);
		System.out.println(bend_mag.getDfltField());
		
		model.run();
		
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
		
		launch(args);	
	}
	
	
	public void run_model_and_plot(Scenario model) {
		
	}
	
	@Override
	public void start(Stage stage) {
		LineChart<Number, Number> line1 = getView1();
		line1.setPrefHeight(600);
		line1.setPrefWidth(900);

		Group root = new Group();
		root.getChildren().add(line1);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("plotstyle.css").toExternalForm());
		
		stage.setScene(scene);
		stage.setTitle("First JavaFX");
		stage.setHeight(700);
		stage.setWidth(1000);
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
