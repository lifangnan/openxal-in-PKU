package openxal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

import connect_mySql.databaseUtil.DBUtil;
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
import xal.smf.data.XMLDataManager;
import xal.smf.impl.Quadrupole;
import xal.tools.beam.CovarianceMatrix;

public class find_fringeK0 {
	
	static ArrayList<Double> position = new ArrayList<>();
	static ArrayList<Double> sigmaX = new ArrayList<>();
	static ArrayList<Double> sigmaY = new ArrayList<>();
	static Double max_posi, min_y, max_y;

	// 连接MySQL服务
	static DBUtil dbUtil = DBUtil.getDBUtil();
	
	
	public static void main(String[] args) throws InterruptedException, JSONException, InstantiationException, ModelException {
//		1. 加载加速器配置
		Accelerator clapa1 = XMLDataManager.acceleratorWithPath("D:\\2021\\clapa1_model\\main.xal");
		
		String seqname = "QuadTrip";
		AcceleratorSeq Acc_Seq = clapa1.getSequence(seqname);
		Quadrupole QH01 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QH01");
		Quadrupole QV02 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QV02");
		Quadrupole QH03 = (Quadrupole) Acc_Seq.getNodeWithId("QuadTrip_Mag:QH03");
		double G1 = 0, G2 = 0, G3 = 0, K0=0;
		
		Context context = ZMQ.context(1);
		Socket socket = context.socket(ZMQ.REP);
		socket.bind("tcp://*:7732");
		Thread.sleep(1000);
		
		String sql;
		while (true) {
			byte[] request;
			// 等待下一个client端的请求
			// 等待一个以0结尾的字符串
			// 忽略最后一位的0打印
			request = socket.recv(0);
//			Map<String, Double> received = getstring	
			JSONObject params = new JSONObject(new String(request));
//			G1 = params.getDouble("G1");
//			G2 = params.getDouble("G2");
//			G3 = params.getDouble("G3");
			K0 = params.getDouble("K0");
//			
//			QH01.setDfltField(G1);
//			QV02.setDfltField(G2);
//			QH03.setDfltField(G3);
			
			QH01.setFringeFieldIntegralK0(K0);
			QV02.setFringeFieldIntegralK0(-K0);
			QH03.setFringeFieldIntegralK0(K0);
			
			Double[] sigma = do_simulation(Acc_Seq);
////			sql = String.format("UPDATE bayes_optimization SET sigmaX=%f, sigmaY=%f, finished=%d WHERE id=%d", sigma[0], sigma[1], 1, rs.getInt("id"));
//			sql = String.format("INSERT INTO bayes_optimization(G1, G2, G3, sigmaX, sigmaY, finished) VALUES(%f, %f, %f, %f, %f, %d)", G1, G2, G3, sigma[0], sigma[1], 1);
//			System.out.println(sql);
//			System.out.println( dbUtil.exeute(sql) );
//			executeUpdate_result = dbUtil.executeUpdate(sql);
//			System.out.println(executeUpdate_result);
 
			// 返回sigma给客户端
			String replyString = String.format("{\"sigmaX\":%f, \"sigmaY\":%f}", sigma[0], sigma[1]);
			byte[] reply = replyString.getBytes();
			socket.send(reply, ZMQ.NOBLOCK);
		}
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
}
