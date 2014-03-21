package application;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import application.utils.Data;
import application.utils.FileData;
import application.utils.IpPortPair;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

public class Server extends Thread {


	static int PORT = 8000; 
	static String PATH = "H:\\Projects\\test3";
	static String IP = null;
	static HttpServer server;

	public Server(int port, String path){
		PORT = port;
		PATH = path;
		this.start();
	}

	public Server(int port){
		PORT = port;

		this.start();
	}

	public void run(){
		try{

			IP = InetAddress.getLocalHost().getHostAddress();
			server = HttpServer.create(new InetSocketAddress(PORT), 0);

			server.createContext("/searchfile", new SearchHandler());
			server.createContext("/foundfile", new FoundFileHandler());
			server.createContext("/getfile", new GetHandler());
			server.setExecutor(null); 
			//server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(50));
			server.start();

			Main.addLog("Server started at port " + PORT);
			Main.addLog("Wazaa path " + PATH);
		}catch(Exception e){
			e.printStackTrace();
		}

	}




	class SearchHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange e) throws IOException {

			String query = e.getRequestURI().getQuery();

			if(query == null){
				String response = "2";
				e.sendResponseHeaders(400, response.length());
				OutputStream os = e.getResponseBody();
				os.write(response.getBytes());
				os.close();
				return;

			}

			Map<String, String> params = queryToMap(query);

			if(params.containsKey("name") && params.containsKey("sendip") && params.containsKey("sendport") && params.containsKey("ttl")){

				List<Path> files =  search(params.get("name"));

				if(files.size() == 0){

					String response = "1";
					e.sendResponseHeaders(200, response.length());
					OutputStream os = e.getResponseBody();
					os.write(response.getBytes());
					os.close();
				}else{

					String response = "0";
					e.sendResponseHeaders(200, response.length());
					OutputStream os = e.getResponseBody();
					os.write(response.getBytes());
					os.close();

					String content = "{";

					if(params.containsKey("id"))
						content +=	"\"id\":\"" + params.get("id") + "\",";

					content +=	"\"files\":[";


					int len = files.size();
					for(Path file : files){

						if(len == 1)
							content += "{" + "\"ip\":\"" + IP +  "\"," +  "\"port\":\"" + PORT + "\"," + "\"name\":\"" + file.getFileName().toString() +  "\"}";
						else 
							content += "{" + "\"ip\":\"" + IP +  "\"," +  "\"port\":\"" + PORT + "\"," + "\"name\":\"" + file.getFileName().toString() +  "\"},";
						--len;
					}


					content += "]}";

					new FoundFileReq(params.get("sendip"), params.get("sendport"), content);

				}


				int ttl = Integer.parseInt(params.get("ttl"));
				if(ttl > 1){

					String id = null;
					String[] noAsk = null;

					if(params.containsKey("id")){
						id = params.get("id");
					}
					if(params.containsKey("noask")){
						noAsk = params.get("noask").split("_");
					}

					
					
					Loop:
					for(IpPortPair m : Main.machines){
						
						if(noAsk!=null)
							for(String ipnoask : noAsk)
								if(ipnoask.equals(m.getIp()))
									continue Loop;
						
						new SearchQuery(m.getIp(), Integer.toString(m.getPort()), params.get("sendip"), params.get("sendport"), params.get("name"), ttl - 1 , id, noAsk + "_" + IP);

					}

				}

			}else{
				String response = "2";
				e.sendResponseHeaders(400, response.length());
				OutputStream os = e.getResponseBody();
				os.write(response.getBytes());
				os.close();

			}




		}


	}

	class FoundFileHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange e) throws IOException {

			String content = "";

			BufferedReader br = new BufferedReader(new InputStreamReader(e.getRequestBody()));

			String line = "";

			while( (line = br.readLine()) != null){

				content += line;

			}

			String response = "0";
			e.sendResponseHeaders(200, response.length());
			OutputStream os = e.getResponseBody();
			os.write(response.getBytes());
			os.close();

			String decodedData = URLDecoder.decode( content , "UTF-8"); 

			System.out.println("content from foundfile  " + decodedData);

			Data data = new Gson().fromJson(decodedData, Data.class);
			for(FileData f : data.files){
				if(data.id != null)
					f.id = data.id;
				Main.addToFilesList(f);
			}


		}

	}



	class GetHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {

			String query = t.getRequestURI().getQuery();
			Map<String, String> params = queryToMap(query);
			String fname = params.get("fullname");
			File file = new File (PATH + File.separator + fname);

			if(file.exists() && file.isFile()){

				Headers h = t.getResponseHeaders();
				h.add("Content-Type", Files.probeContentType( file.toPath() ));

				byte [] bytearray  = new byte [(int)file.length()];
				FileInputStream fis = new FileInputStream(file);
				@SuppressWarnings("resource")
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(bytearray, 0, bytearray.length);

				t.sendResponseHeaders(200, file.length());
				OutputStream os = t.getResponseBody();
				os.write(bytearray,0,bytearray.length);
				os.close();
				Main.addLog("Uploaded file " + fname);

			}else{
				t.sendResponseHeaders(404, 0);
			}
		}
	}



	public Map<String, String> queryToMap(String query){
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length>1) {
				result.put(pair[0], pair[1]);
			}else{
				result.put(pair[0], "");
			}
		}
		return result;
	}


	public List<Path> search(final String s){

		Path filesPath  = FileSystems.getDefault().getPath(PATH);

		Filter<Path> filter = new Filter<Path>() {
			@Override
			public boolean accept(Path entry) throws IOException {
				if(entry.toFile().isDirectory()) return false;
				String actual = entry.getFileName().toString();
				return actual.contains(s);
			}
		};

		List<Path> l = new LinkedList<Path>();

		try( DirectoryStream<Path> ds = Files.newDirectoryStream(filesPath, filter) ){

			for(Path file : ds){
				l.add( file.getFileName() );
				System.out.println(file.toString());

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return l;

	}

	public void stopServer(){
		server.stop(NORM_PRIORITY);
	}







}