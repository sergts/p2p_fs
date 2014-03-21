package application.utils;

import java.util.ArrayList;

public class IpPortPair  extends ArrayList<String>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getIp(){
		return get(0);
	}
	
	@Override
	public String toString(){
		return "ip=" + get(0) + ", port=" + get(1);
		
	}
	
	public int getPort(){
		return Integer.parseInt(get(1));
	}
	
	
}
