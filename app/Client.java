package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.collections.ObservableList;

public class Client extends Thread {
	
	

	
	private static String ip;
	private static int port;
	private static String req;
	private static String resp;
	//ObservableList<String> list;
	Main app;
	
	
	
	public Client(Main app, String ip, int port, String req){
		this.ip = ip;
		this.port = port;
		this.req = req;
		this.start();
		System.out.println("client started");
		//list = files;
		this.app = app;
	}
	
	
	
	
	public void run(){
		
		if(req == null){
			resp = getAll();
		}
		else{
			resp = searchFile(req);
		}
		
		System.out.println(resp);
		//list.clear();
		app.clearFileList();
		
		Data data = new Gson().fromJson(resp, Data.class);
		
		System.out.println("HH");
		//String fileNames = data.getFiles();
		//System.out.println("HH" + fileNames);
		
		/*
		Gson gson = new Gson();
		Type collectionType = new TypeToken<List<FileName>>(){}.getType();
		List<FileName> names = gson.fromJson(fileNames, collectionType);*/
		
		
		for(FileName fn : data.files){
			System.out.println(fn.name);
			//list.add(fn.name);
			app.addToFilesList(fn.name);
		}
		
		
		
		
		
		//list.add(resp);
		
	}
	
	
	public String getResp(){
		return resp;
	}
	
	
	public String getAll(){
		
		HttpURLConnection connection = null;
		String returnValue = "";
		try {
			URL url = new URL("http://" + ip + ":" + port);
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
			e.printStackTrace();
		}
		return returnValue;
		
		
	}
	
	public String searchFile(String fileName){
		HttpURLConnection connection = null;
		String returnValue = "";
		try {
			URL url = new URL("http://" + ip + ":" + port + "/searchfile?name=" + fileName);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String output = null;
			
			
			while((output = br.readLine()) != null){
				System.out.println(output);
				returnValue += output;
		
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
	}

}


class Data{
	List<FileName> files;
	
	//public List<FileName> getFiles() { return files; }
	//public void setFiles(List<FileName> files) { this.files = files; }
	

	
}
class FileName{
	String name;
	//public String getFileName(){return fileName;}
	//public void setFileName(String fN){
		//fileName = fN;
	//}
}


