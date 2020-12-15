import java.util.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server {
	
	public static int port = 8080;
	public static String path = "src/";
	public static Boolean v = false;
	public static Boolean d = false;
	
    public static void main(String[] args) throws Exception {
    	Scanner command = new Scanner(System.in);
        System.out.print(">");
        String input = command.nextLine();
        String[] data = input.split(" ");
        if(data[0].equals("httpfs")) {
        	if(data.length >= 2) {
        		for(int i=1;i<data.length;i++) {
	        		if(data[i].equals("-p")) {
	            		port = Integer.parseInt(data[i+1]);
	            		i+=1;
	            	}else if(data[i].equals("-d")) {
	            		d = true;
	            		path = data[i+1];
	            		i+=1;
	            	}else if(data[i].equals("-v")) {
	            		v = true;
	            	}
        		}
        	}
        	System.out.print("Server is up and running");
        	if(v) {
        		System.out.print(" with port "+port);
        	}
        	if(d) {
        		System.out.print(" and path "+path);
        	}
        	System.out.println();
        	UDPrunning();
        }
    }

    public static void UDPrunning() throws IOException {
    	DatagramSocket dss = new DatagramSocket(port); 
        byte[] receive = new byte[Packet.MAX_LEN];
        String reply = "";
        int dType = 0;//0-data 1-ack
        DatagramPacket DpReceive = null;
        ServerLib slib = new ServerLib();
        while (true) { 
            DpReceive = new DatagramPacket(receive, receive.length); 
            dss.receive(DpReceive); 
            Packet p = Packet.fromBytes(DpReceive.getData());
            String str = new String(p.getPayload());
            //System.out.println(str);
            // Connections to the ServerLib
            // .....
            if(str.trim().equalsIgnoreCase("hello world")){
            	reply = "Hello World Received";
            	dType = 1;
            }else if(str.toLowerCase().contains("Get".toLowerCase())) {
            	reply = slib.get(path, str);
            	dType = 0;
            }else if(str.toLowerCase().contains("Post".toLowerCase())) {
            	reply = slib.post(path, str);
            	dType = 0;
            }
            //To Client Starts
            DatagramSocket dsc = new DatagramSocket();  
            InetAddress ip = InetAddress.getLocalHost(); 
            if(reply.getBytes().length > 1013) {
            	int bIndex = 0;
            	int eIndex = 1013;
            	int sNum = 1;
            	while(bIndex < reply.getBytes().length) {
            		Packet p1 = new Packet.Builder()
                            .setType(dType)
                            .setSequenceNumber(sNum)
                            .setPortNumber(p.getPeerPort())
                            .setPeerAddress(p.getPeerAddress())
                            .setPayload(reply.substring(bIndex, eIndex).getBytes())
                            .create();
                    bIndex = eIndex;
                    sNum++;
                    if(eIndex+1013 >= reply.getBytes().length) {
                    	eIndex = reply.getBytes().length;
                    }else {
                    	eIndex += 1013;
                    }
                    byte[] sendPacket = new byte[p1.MAX_LEN];
                    DatagramPacket DpSend = new DatagramPacket(p1.toBytes(), p1.toBytes().length, ip.getLocalHost(), 3000);
                    dss.send(DpSend);
            	}
            }else {
            	Packet p1 = new Packet.Builder()
                        .setType(dType)
                        .setSequenceNumber(p.getSequenceNumber())
                        .setPortNumber(p.getPeerPort())
                        .setPeerAddress(p.getPeerAddress())
                        .setPayload(reply.getBytes())
                        .create();
                byte[] sendPacket = new byte[p1.MAX_LEN];
                DatagramPacket DpSend = new DatagramPacket(p1.toBytes(), p1.toBytes().length, ip.getLocalHost(), 3000);
                dss.send(DpSend);
            }
        }
    }
}

