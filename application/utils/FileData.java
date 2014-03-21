package application.utils;

public class FileData {
	 public String name;
	 public String ip;
	 public int port;
	 public String id = null;
	 
	 @Override 
	 public String toString(){
		 return name + "   " + ip + ":" + port;
	 }
	 
	 @Override
	 public boolean equals(Object o){
		 
		 if(this == o) return true;
		 if(!(o instanceof FileData)) return false;
		 FileData f = (FileData)o;
		 
		 
		return name.equals(f.name) && ip.equals(f.ip) && (port == f.port);
		 
	 }
}
