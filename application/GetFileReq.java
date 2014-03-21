package application;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetFileReq extends Thread {
	
	String path;
	String ip;
	String port;
	String fname;
	
	GetFileReq(String path, String ip, String port, String filename){
		this.path = path;
		this.ip = ip;
		this.port = port;
		fname = filename;
		this.start();
	}
	
	
	public void run(){
		
		try (InputStream in = URI.create( "http://" + ip + ":" + port + "/getfile?fullname=" + fname).toURL().openStream()) {
	        Files.copy(in, Paths.get(path + File.separator + fname));
	        Main.addLog("File " + fname + " downloaded to " + path);
	    }catch(Exception e){
	    	System.out.println("Could not download file " + fname + "  " + ip + " " + port);
	    }
		
	}
	
	
	

}
