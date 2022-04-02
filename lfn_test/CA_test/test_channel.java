package CA_test;

import xal.ca.Channel;
import xal.ca.ChannelFactory;
import xal.ca.ConnectionException;
import xal.ca.GetException;
import xal.ca.PutException;

public class test_channel {

	public static void main(String[] args) throws ConnectionException, GetException, PutException {
		// TODO Auto-generated method stub
		ChannelFactory caFac = ChannelFactory.defaultFactory();
		Channel ca = caFac.getChannel("lfn:test:t");
		System.out.println(ca.getValFlt());
//		ca.putVal(-10);
//		System.out.println(ca.getValFlt());
	}

}
 