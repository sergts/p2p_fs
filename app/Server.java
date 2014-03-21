package app;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;


public class Server extends Thread {

	private static String PATH = "H:\\Projects\\test3";
	
	public int portG;
	
	public Server(int port){
		
		
		this.portG = port;
		this.start();
		
	}
	
	





	public void run() {
				//System.out.println(search("cat"));
		
				int port = portG;
				
			
				try(ServerSocket ss = new ServerSocket(port)){
					
					System.out.println("Server started at " + port);
				
					while(true){
						Socket conn = ss.accept();
						
						BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						
						String req = in.readLine();
						System.out.println(req);
						
						String name = "";
						
						if(  req.indexOf("/searchfile?name=") != -1)
							name = req.substring( req.indexOf("/searchfile?name=") + 17, req.length() - 9);
						
						
						
						
						DataOutputStream out = new DataOutputStream(conn.getOutputStream());
						
						System.out.println(name);
						
						List<Path> l = search(name);
						for(Path file : l){
							System.out.println(file.getFileName());
						}
						
						String output = "HTTP/1.0 200 OK\r\nConnection: close\r\nContent-type: text/html\r\nContent-Length: ";
						String content = "";
						String content2 = "{\"files\":[";
						if(l.size() == 0){
							
							
							
							
							
							content = "File " + name  + " not found \n";
							
							
						}
						else{
							
							
							int len = l.size();
							for(Path file : l){
								content += file.getFileName().toString() + "\n";
								if(len == 1)
									content2 += "{\"name\":\"" + file.getFileName().toString() +  "\"}";
								else 
									content2 += "{\"name\":\"" + file.getFileName().toString() +  "\"},";
								--len;
							}
							
							//content2 = content2.substring(0, content2.length() - 1);
							
							
						}
						
						content2 += "]}";
						
						/*
						String resp = "Searching for file '" + name + "' results: \n";
						
						output += (content.length() + resp.length()) + "\r\n";
						output += "\r\n";
						output += resp;
						output += content;*/
						
						
						output += (content2.length()) + "\r\n";
						output += "\r\n";
						
						output += content2;
						
						
						out.writeBytes(output);
						
						
						out.close();
						conn.close();
						
					}
				}catch(Exception e2){
					e2.printStackTrace();
				}


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
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return l;
		
	}
	
	
	

}
