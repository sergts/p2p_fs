package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchQuery extends Thread {

	String toIp;
	String toPort;
	String sIp;
	String sPort;
	String fname;
	int ttl;
	String id = null;
	String noAsk = null;
	
	
	SearchQuery(String toIp, String toPort, String senderIp, String senderPort, String filename, int ttl, String id, String noAsk){
		
		this.toIp = toIp;
		this.toPort = toPort;
		sIp = senderIp;
		sPort = senderPort;
		fname = filename;
	    this.ttl = ttl;
	    this.id = id;
	    this.noAsk = noAsk;
	    this.start();
		
	}
	
	public void run(){
		
		
		System.out.println("searchfile response " + searchFile());
		
	}
	
	
	
	public String searchFile(){
		HttpURLConnection connection = null;
		String returnValue = "";
		try {
			String req = "http://" + toIp + ":" + toPort + "/searchfile?name=" + fname + "&sendip=" + sIp + "&sendport=" + sPort + "&ttl=" + ttl;
			if(id!=null) req += "&id=" + id;
			if(noAsk!=null) req += "&noask=" + noAsk;
			URL url = new URL(req);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String output = null;
			
			
			while((output = br.readLine()) != null){
				
				returnValue += output;
		
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return returnValue;
	}
	
	
}
