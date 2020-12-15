import java.io.*;

public class ServerLib {

	public String get(String path, String cmd) throws IOException {
        String reply = "";
        String[] kwords = cmd.trim().split("/");
        String[] kw =cmd.split(" ");
        String[] s=new String[2];
        if(kw.length==5) {
        	s=kw[3].split(":");
        }
        if(kwords.length == 4) {
        	File file = new File(path);
        	File filesList[] = file.listFiles();
        	if(kw[2].equalsIgnoreCase("-h")) {
        		if(s[0].equalsIgnoreCase("Content-Type")) {
        			for(File files : filesList) {
            			if(files.toString().contains("."+s[1])){
            				reply = reply.concat(files.getName()+"\n");
            			}
        			}
        		}else {
        			if(s[1].equalsIgnoreCase("attachment")) {
        				for(File files : filesList) {
	        				reply = reply.concat(getData(files));
	        			}
        				File f1 = new File("src/output.txt");
                        FileWriter fr = new FileWriter(f1);
                        fr.write(reply);
                        fr.close();
        				return "Success";
        			}else {
	        			for(File files : filesList) {
	        				reply = reply.concat(getData(files));
	        			}
        			}
        		}
        	}else {
	            for(File files : filesList) {
	           		reply = reply.concat(files.getName()+"\n");
	            } 
	        }
        	return reply;
        }else {
        	File file = new File(path+kwords[4]); 
        	reply = getData(file);
        }    
        return reply;
    }

	public String getData(File f) throws IOException {
		String reply="";
    	if(f.exists() && f.isFile()) {
    		BufferedReader br = new BufferedReader(new FileReader(f)); 
        	String st; 
        	while ((st = br.readLine()) != null) {
        		reply = reply.concat(st+"\n");
        	} 
    	}else {
    		reply = "File not Found.";
    	}
		return reply;		
	} 
	
	
    public String post(String path, String cmd) throws IOException {
    	String reply = "";
        String[] kwords = cmd.split("/");
        String[] kw =kwords[4].split(" ");
        String output = kwords[4].substring(kwords[4].indexOf("'")+1,kwords[4].trim().length()-1);
        String filename = kw[0];
        try {
        	String fill = path+filename;
            Writer myfiles = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fill), "utf-8"));
            myfiles.write(output);
            myfiles.close();
            reply = "Success";
        } catch (IOException e) {
        	reply = "Error";
            e.printStackTrace();
        }
        return reply;
	}
}


//		LA2 Commands
//		httpc get http://localhost:8080/Get/
//		httpc get http://localhost:8080/Get/foo
//		httpc post http://localhost:8080/POST/foo -d 'network'
//		httpc get -h Content-Type:txt http://localhost:8080/Get/
//		httpc get -h Content-Disposition:attachment http://localhost:8080/Get/
//		httpc get -h Content-Disposition:inline http://localhost:8080/Get/


//		LA1 Commands
//      httpc help
//      httpc help post
//      httpc help get
//      httpc get http://httpbin.org/get?course=networking&assignment=1
//      httpc get -v http://httpbin.org/get?course=networking&assignment=1
//      httpc post -h Content-Type:application/json --d {"Assignment":1} http://httpbin.org/post
//      httpc post -v -h Content-Type:application/json --d {"Assignment":1} http://httpbin.org/post
//      httpc post -h Content-Type:application/json --d {"Assignment":1} -d {"Assignment":2} --d {"Assignment":3} http://httpbin.org/post
//      httpc post -h Content-Type:application/json -f {} -d {} http://httpbin.org/post
//      httpc post -h Content-Type:application/json -f data.txt http://httpbin.org/post
//      httpc post -h Content-Type:application/json -d {"Assignment":1} -o result.txt http://httpbin.org/post
//      httpc get -v -h Content-Type:application/json https://httpstat.us/302
//      redirect url
//      httpc get -v http://mintox.com/contact
//      httpc get -v http://www.socengine.com/soe