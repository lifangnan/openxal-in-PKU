package optimization_algorithm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import connect_mySql.databaseUtil.DBUtil;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import xal.model.ModelException;
import xal.model.alg.EnvTrackerAdapt;
import xal.model.probe.EnvelopeProbe;
import xal.model.probe.traj.EnvelopeProbeState;
import xal.model.probe.traj.Trajectory;
import xal.sim.scenario.AlgorithmFactory;
import xal.sim.scenario.ProbeFactory;
import xal.sim.scenario.Scenario;
import xal.smf.Accelerator;
import xal.smf.AcceleratorNode;
import xal.smf.AcceleratorSeq;
import xal.smf.data.XMLDataManager;
import xal.smf.impl.Quadrupole;
import xal.tools.beam.CovarianceMatrix;

public class optimize_algorithm_v1{
	
	static ArrayList<Double> position = new ArrayList<>();
	static ArrayList<Double> sigmaX = new ArrayList<>();
	static ArrayList<Double> sigmaY = new ArrayList<>();
	static Double max_posi, min_y, max_y;

	// 连接MySQL服务
	static DBUtil dbUtil = DBUtil.getDBUtil();
	
	public static void main(String[] args) throws InstantiationException, ModelException, SQLException, InterruptedException {
		// TODO Auto-generated method stub
		
//		ZMQ服务端
		Context context = ZMQ.context(1);
		Socket socket = context.socket(ZMQ.REP);
		socket.bind("tcp://*:7732");
		Thread.sleep(1000);
		
		
//		1. 加载加速器配置
		Accelerator clapa1 = XMLDataManager.acceleratorWithPath("D:\\2021\\clapa1_model\\main.xal");
		
		String seqname = "QuadTrip";
		AcceleratorSeq Acc_Seq = clapa1.getSequence(seqname);
		Quadrupole QH01 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QH01");
		Quadrupole QV02 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QV02");
		Quadrupole QH03 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QH03");
//		Double IntK0 = 2e-4;
//		QH01.setFringeFieldIntegralK0(IntK0);
//		QV02.setFringeFieldIntegralK0(-IntK0);
//		QH03.setFringeFieldIntegralK0(IntK0);
		
		
		
//		System.out.println(dbUtil.getConn());
		// 插入参数表，并给出初始的1套参数
//		String[] param = {};
//		ResultSet rs = dbUtil.executeQuery("select * from test", param);
//		while(rs.next()){
//            // 通过字段检索
//            int id  = rs.getInt("id");
//            Double G1 = rs.getDouble("G1");
//            Double G2 = rs.getDouble("G2");
//
//            // 输出数据
//            System.out.print("ID: " + id);
//            System.out.print(", G1: " + G1);
//            System.out.print(", G2: " + G2);
//            System.out.print("\n");
//        }
		
//		dbUtil.exeute("create table if not exists Bayes_optimization(id int primary key," +
//				"G1 double," +
//				"G2 double," +
//				"G3 double," +
//				"sigmaX double," +
//				"sigmaY double," +
//				"finished int");
		boolean exeute_success = true;
//		exeute_success = dbUtil.exeute("insert into bayes_optimization(id, G1, G2, G3, sigmaX, sigmaY, finished) values(0, 13.667, -10.6, 12.339, -1.0, -1.0, 0)");
//		System.out.println(exeute_success);
		
		int db_id = 0;
		double G1 = 0, G2 = 0, G3 = 0;
		String sql = "";
		int executeUpdate_result;
		while(true) {
//			String[] param = {String.valueOf(db_id)};
			ResultSet rs = dbUtil.executeQuery("select * from bayes_optimization");
			
			while(rs.next()) {
				if(rs.getInt("finished") == 1) {continue;}
				G1 = rs.getDouble("G1");
				G2 = rs.getDouble("G2");
				G3 = rs.getDouble("G3");
				QH01.setDfltField(G1);
				QV02.setDfltField(G2);
				QH03.setDfltField(G3);
				
				Double[] sigma = do_simulation(Acc_Seq);
				sql = String.format("UPDATE bayes_optimization SET sigmaX=%f, sigmaY=%f, finished=%d WHERE id=%d", sigma[0], sigma[1], 1, rs.getInt("id"));
				System.out.println(sql);
				executeUpdate_result = dbUtil.executeUpdate(sql);
				System.out.println(executeUpdate_result);
			}
			
			break;
//			launch(args);
		}
		
//		launch(args);
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
		System.out.println("simgaX="+ last_sigmaX + "  sigmaY=" + last_sigmaY);
		
		Double[] last_sigma = {last_sigmaX, last_sigmaY};
		return last_sigma;
	}
	
	
	
	
//	@Override
//	public void start(Stage stage) {
//		LineChart<Number, Number> line1 = getView1();
//		line1.setPrefHeight(600);
//		line1.setPrefWidth(900);
//
//		Group root = new Group();
//		root.getChildren().add(line1);
//		
//		Scene scene = new Scene(root);
//		scene.getStylesheets().add(getClass().getResource("plotstyle.css").toExternalForm());
//		
//		stage.setScene(scene);
//		stage.setTitle("First JavaFX");
//		stage.setHeight(700);
//		stage.setWidth(1000);
//		stage.show();
//	}
	
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
