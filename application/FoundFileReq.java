package application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.List;

public class FoundFileReq extends Thread {

	
	String toIp;
	String toPort;
	List<Path> files;
	String content;
	
	public FoundFileReq(String toIp, String toPort, String content){
		
		
		this.toIp = toIp;
		this.toPort = toPort;
		this.content = content;
		this.start();
		
	}
	
	
	public void run(){
		
	
		
		if( !(  (toIp.equals("127.0.0.1") || toIp.equals(Server.IP)) && (toPort.equals(Integer.toString(Server.PORT)))   ))
			foundFiles(content);

		
		
	}
	
	
	public String foundFiles(String content){
		String returnValue = "";
		try {
			String rawData = content;
			String type = "application/x-www-form-urlencoded";
			String encodedData = URLEncoder.encode( rawData , "UTF-8"); 
			URL u = new URL( "http://" + toIp + ":" + toPort + "/foundfile");
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", type );
			conn.setRequestProperty( "Content-Length", String.valueOf(encodedData.length()));
			OutputStream os = conn.getOutputStream();
			os.write( encodedData.getBytes() );
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
