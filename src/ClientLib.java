import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class ClientLib {

    private String path;
    private String host;
    private String query;
    private String req;
    private int port;

    synchronized String get(String cmd) throws IOException {
        boolean v = false, o = false, r = true;
        ArrayList<String> headers = new ArrayList<>();
        String url = "";
        String[] kwords = cmd.split(" ");
        String b1 = "";
        String b2 = "";
        String ofile = null;
        String answer = "";

        for(int i=0;i<kwords.length;i++){
            if(kwords[i].equals("-v") || kwords[i].equals("--v")){
                v = true;
            }else if(kwords[i].equals("-h") || kwords[i].equals("--h")){
                i += 1;
                headers.add(kwords[i]);
            }else if(kwords[i].equals("-o") || kwords[i].equals("--o")){
                o = true;
                i += 1;
                ofile = kwords[i];
            }else{
                url = kwords[i];
            }
        }

        URL url1 = new URL(url);
        path = url1.getPath();
        host = url1.getHost();
        port = 80;
        query = url1.getQuery();

        Socket s = new Socket(host,port);
        PrintWriter pr = new PrintWriter(s.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        req = "GET "+path;
        if(query!=null){
            req=req.concat("?"+query);
        }
        req = req.concat(" HTTP/1.0\r\nHost:"+host+"\r\n");
        for (String header : headers) {
            req = req.concat(header + "\r\n");
        }
        req = req.concat("\r\n");
        answer += sendReceive(r, v, o, b1, b2, ofile, pr, in, cmd, url);
        return answer;
    }

    synchronized String post(String cmd) throws IOException {
        boolean v = false, d = false, f = false, o = false, r = false;
        ArrayList<String> headers = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();
        ArrayList<String> file = new ArrayList<>();
        String url = "";
        String[] kwords = cmd.split(" ");
        String b1 = "";
        String b2 = "";
        String ofile = null;
        String answer = "";
        int clength = 0;
        for(int i=0;i<kwords.length;i++){
            if(kwords[i].equals("-v") || kwords[i].equals("--v")){
                v = true;
            }else if(kwords[i].equals("-h") || kwords[i].equals("--h")){
                i += 1;
                headers.add(kwords[i]);
            }else if(kwords[i].equals("-d") || kwords[i].equals("--d")){
                d = true;
                i += 1;
                data.add(kwords[i]);
            }else if(kwords[i].equals("-f") || kwords[i].equals("--f")){
                f = true;
                i += 1;
                file.add(kwords[i]);
            }else if(kwords[i].equals("-o") || kwords[i].equals("--o")){
                o = true;
                i += 1;
                ofile = kwords[i];
            }else{
                url = kwords[i];
            }
        }
        if(d&&f){
            answer += "Can't use -d and -f together.\n";
        }else {
            for(String ifile : file){
                File efile = new File("src/"+ifile);
                if(efile.exists()) {
                    BufferedReader on = new BufferedReader(new FileReader(efile));
                    String file_data;
                    while ((file_data=on.readLine()) != null){
                        data.add(file_data);
                    }
                }else{
                    answer += "File don't exists.\n";
                    return answer;
                }
            }

            URL url1 = new URL(url);
            path = url1.getPath();
            host = url1.getHost();
            port = 80;
            query = url1.getQuery();

            Socket s = new Socket(host, port);
            PrintWriter pr = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            req = "POST " + path;
            if (query != null) {
                req = req.concat("?" + query);
            }
            req = req.concat(" HTTP/1.0\r\nHost:" + host + "\r\n");

            for (String datum : data) {
                clength = clength + datum.length();
            }
            req = req.concat("Content-Length:" + clength + "\r\n");
            for (String header : headers) {
                req = req.concat(header + "\r\n");
            }
            if (data.size() != 0) {
                for (String datum : data) {
                    req = req.concat("\r\n" + datum);
                }
            } else {
                req = req.concat("\r\n");
            }
            answer += sendReceive(r, v, o, b1, b2, ofile, pr, in, cmd, url);
            return answer;
        }
		return answer;
    }

    private String sendReceive(boolean r, boolean v, boolean o, String b1, String b2, String ofile, PrintWriter pr, BufferedReader in, String cmd, String url) throws IOException {

        pr.write(req);
        pr.flush();
        String answer = "";
        String new_url = null;
        String reply = in.readLine();
        if(reply.contains("300") || reply.contains("301") || reply.contains("302") || reply.contains("303") || reply.contains("304") || reply.contains("307")){
            if(!r){
                answer += "Redirection is not allowed for POST.\n";
                return answer;
            }
            System.out.println("--------------------------------------------------------");
            System.out.println("Redirection required.");
            System.out.println("--------------------------------------------------------");
            while (reply.length() != 0) {
                if(reply.contains("Location:")){
                    new_url = reply.replace("Location: ","");
                }
                b1 = b1.concat(reply + "\n");
                reply = in.readLine();
            }
            System.out.println(b1);
            b1 = "";
            //System.out.println("old url : "+url);
            //System.out.println("new url : "+new_url);
            if(new_url.equals(url)){
                answer += "--------------------------------------------------------\n";
                answer += "Redirecting url is same as the old one.\n";
                answer += "--------------------------------------------------------\n";
                return answer;
            }else{
                System.out.println("--------------------------------------------------------");
                System.out.println("Redirecting from old to new.");
                System.out.println("--------------------------------------------------------");
                cmd = cmd.replace(url,new_url);
                get(cmd);
            }
        }else{
            while (reply.length() != 0) {
                b1 = b1.concat(reply + "\n");
                reply = in.readLine();
            }
            reply = in.readLine();
            while (reply != null) {
                b2 = b2.concat(reply + "\n");
                reply = in.readLine();
            }
            if(o){
                File f1 = new File(ofile);
                FileWriter fr = new FileWriter(f1);
                if(v){
                    fr.write(b1);
                }
                fr.write(b2);
                fr.close();
            }else {
                if (v) {
                    answer += b1+"\n";
                }
                answer += b2+"\n";
            }
        }
		return answer;
    }
}

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