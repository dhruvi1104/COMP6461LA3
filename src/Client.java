import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;


public class Client{

	static int sNum = 1;
	static int dType = 0;
	
    public static void main(String[] args) throws IOException {
        System.out.println("Welcome to cUrl Application");
        read();
        System.out.println("Thank you for using cUrl Application");
    }

    public static void read() throws IOException {
    	ClientLib hlib = new ClientLib();
        Scanner request = new Scanner(System.in);
        int port = 8080;
        //UDP for client to server
        DatagramSocket dsc = new DatagramSocket();  
        InetAddress ip = InetAddress.getLocalHost(); 
        byte buf[] = new byte[65535]; 
        
       // Packet p = new Packet(0,1,ip.getLocalHost(),port,buf);
        String msg = "Hello World";
        dType = 0;
        Packet p = new Packet.Builder()
                .setType(dType)
                .setSequenceNumber(sNum)
                .setPortNumber(8080)
                .setPeerAddress(ip)
                .setPayload(msg.getBytes())
                .create();

        sNum++;
        byte[] sendPacket = new byte[p.MAX_LEN];
        DatagramPacket DpSend = new DatagramPacket(p.toBytes(), p.toBytes().length, ip.getLocalHost(), 3000);
        dsc.send(DpSend);
    
        byte[] receive = new byte[Packet.MAX_LEN];
    	DpSend = new DatagramPacket(receive, receive.length); 
        dsc.receive(DpSend); 
        Packet p1 = Packet.fromBytes(DpSend.getData());
        String s = new String(p1.getPayload());
        if(p1.getType()==1) {
        	System.out.println("Acknoledgement of sequence : "+p1.getSequenceNumber());
        }else {
        	System.out.println("Data : ");
        }
        System.out.println(s);
        
        while (true) {
        	System.out.println("Do you want multi-client-request ? (y/n)");
        	String multi = request.nextLine();
        	if(multi.equals("y")) {
        		Runnable runnable =
		        () -> {
		        	String req = "httpc post http://localhost:8080/POST/foo -d 'nooooooooooooo'";
		        	byte buf1[] = new byte[65535];
		        	try
		        	{
		        		serverCall(req, port, dsc, buf1, hlib, ip, sNum);
		        	}catch (Exception e) {
						e.printStackTrace();
					}
		        };
		        
		        Runnable runnable1 =
		        () -> {
		        	String req = "httpc get http://localhost:8080/Get/";
		        	byte buf1[] = new byte[65535];
		        	try
		        	{
		        		serverCall(req, port, dsc, buf1, hlib, ip, sNum);
		        	}catch (Exception e) {
						e.printStackTrace();
					}
		        };
		        
		        Runnable runnable2 =
		        () -> {
		        	String req = "httpc post http://localhost:8080/POST/foo -d 'networkkkkkkkkkk'";
		        	byte buf1[] = new byte[65535];
		        	try
		        	{
		        		serverCall(req, port, dsc, buf1, hlib, ip, sNum);
		        	}catch (Exception e) {
						e.printStackTrace();
					}
		        };
		        
		        Runnable runnable3 =
		        () -> {
		        	String req = "httpc get http://localhost:8080/Get/foo";
		        	byte buf1[] = new byte[65535];
		        	try
		        	{
		        		serverCall(req, port, dsc, buf1, hlib, ip, sNum);
		        	}catch (Exception e) {
						e.printStackTrace();
					}
		        };
		        
		        Runnable runnable4 =
		        () -> {
		        	String req = "httpc get http://localhost:8080/Get/foo";
		        	byte buf1[] = new byte[65535];
		        	try
		        	{
		        		serverCall(req, port, dsc, buf1, hlib, ip, sNum);
		        	}catch (Exception e) {
						e.printStackTrace();
		        	}
		        };
				        
		        Runnable runnable5 =
		        () -> {
		        	String req = "httpc get http://localhost:8080/Get/foo.txt";
		        	byte buf1[] = new byte[65535];
		        	try
		        	{
		        		serverCall(req, port, dsc, buf1, hlib, ip, sNum);
		        	}catch (Exception e) {
						e.printStackTrace();
					}
		        };
				        
		        Thread t = new Thread(runnable);
		        Thread t1 = new Thread(runnable1);
		        Thread t2 = new Thread(runnable2);
		        Thread t3 = new Thread(runnable3);
		        Thread t4 = new Thread(runnable4);
		        Thread t5 = new Thread(runnable5);
		        
		        t.start();
		        t1.start();
		        t2.start();
		        t3.start();
		        t4.start();
		        t5.start();
		        
		        System.out.println("\nDo you want to continue??(y/n)");
                Scanner con=new Scanner(System.in);
                String ans = con.nextLine();
                if(ans.equalsIgnoreCase("n")){
                    break;
                }
        	}else {
        		System.out.println("Please enter your request");
                System.out.println("Request: ");
                String req = request.nextLine();
                
                serverCall(req, port, dsc, buf, hlib, ip, sNum);
                
                System.out.println("\nDo you want to continue??(y/n)");
                Scanner con=new Scanner(System.in);
                String ans = con.nextLine();
                if(ans.equalsIgnoreCase("n")){
                    break;
                }
        	}
        }
    }
    
    public static void serverCall(String req, int port, DatagramSocket dsc, byte buf[], ClientLib hlib, InetAddress ip, int sNum) throws IOException {
    	
        String[] kwords = req.split(" ");
        int noOfKwords = kwords.length;
        for (int i = 0; i < noOfKwords; i++) {
            kwords[i] = kwords[i].trim();
            if(kwords[i].contains("localhost")) {
            	port = Integer.parseInt(kwords[i].subSequence(17, 21).toString());
            }
        }
        int flag = 0;
        int j = 0;
        do {
            if (kwords[j].compareTo("httpc") == 0) {
                j++;
                switch (kwords[j]) {
                    case "help":
                        try {
                            j++;
                            if (j < noOfKwords) {
                                if (kwords[j].compareTo("get") == 0 && noOfKwords == 3) {
                                    System.out.println("httpc help get");
                                    System.out.println("usage: httpc get [-v] [-h key:value] URL");
                                    System.out.println("Get executes a HTTP GET request for a given URL.");
                                    System.out.println("	-v	Prints the detail of the response such as protocol, status, and headers.");
                                    System.out.println("	-h	 key:value Associates headers to HTTP Request with the format 'key:value'.");
                                    flag = 1;
                                    break;
                                } else if (kwords[j].compareTo("post") == 0 && noOfKwords == 3) {
                                    System.out.println("httpc help post");
                                    System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL");
                                    System.out.println("Post executes a HTTP POST request for a given URL with inline data or from file.");
                                    System.out.println("	-v	 Prints the detail of the response such as protocol, status, and headers.");
                                    System.out.println("	-h	 key:value Associates headers to HTTP Request with the format 'key:value'.");
                                    System.out.println("	-d	 string Associates an inline data to the body HTTP POST request.");
                                    System.out.println("	-f	 file Associates the content of a file to the body HTTP POST request.");
                                    System.out.println("Either [-d] or [-f] can be used but not both.");

                                    flag = 1;
                                    break;
                                } else {
                                    System.out.println("[1]Invalid command.");
                                    System.out.println("Use \"httpc help [command]\" for more information about a command.");
                                    flag = 1;
                                    break;
                                }
                            } else {
                                System.out.println("httpc help");
                                System.out.println("httpc is a curl-like application but supports HTTP protocol only.");
                                System.out.println("Usage:");
                                System.out.println("	httpc command [arguments]");
                                System.out.println("The commands are:");
                                System.out.println("	get 	executes a HTTP GET request and prints the response.");
                                System.out.println("	post 	executes a HTTP POST request and prints the response.");
                                System.out.println("	help	 prints this screen.");
                                System.out.println("Use \"httpc help [command]\" for more information about a command.");
                                flag = 1;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                        break;
                    case "get":
                        try {
                            j++;
                            String str = "";
                            String[] req1 = req.split(" ",3);
                            System.out.println();
                            
                            Packet p = new Packet.Builder()
                                    .setType(0)
                                    .setSequenceNumber(sNum)
                                    .setPortNumber(port)
                                    .setPeerAddress(ip)
                                    .setPayload(req.getBytes())
                                    .create();
                            sNum++;
                            System.out.println("Request : " + req);
                            System.out.print("Reply : ");
                            if(req1[2].contains("localhost")) {
                            	//UDP request
                                DatagramPacket DpSend = new DatagramPacket(p.toBytes(), p.toBytes().length, ip.getLocalHost(), 3000);
                                dsc.send(DpSend);
                                //UDP response
                                dsc.setSoTimeout(1000);
                                while(true) {
                                	try {
                                		byte[] receive = new byte[p.MAX_LEN];
                                        DpSend = new DatagramPacket(receive, receive.length);
                                        dsc.receive(DpSend);
                                        Packet p1 = Packet.fromBytes(DpSend.getData());
                                        str = new String(p1.getPayload());
                                        System.out.println(str);
                                	}catch(SocketTimeoutException e) {
                                		//System.out.println("Timeout!!!");
                                		break;
                                	}
                                }
                            }else {
                            	str = hlib.get(req1[2]);
                            	System.out.println(str);
                            }
                        }
                        catch (Exception ignored) {
                        }
                        flag=1;
                        break;
                    case "post":
                        try {
                            j++;
                            String str = "";
                            String[] req1 = req.split(" ",3);
                            System.out.println();
                            Packet p = new Packet.Builder()
                                    .setType(0)
                                    .setSequenceNumber(sNum)
                                    .setPortNumber(port)
                                    .setPeerAddress(ip)
                                    .setPayload(req.getBytes())
                                    .create();
                            sNum++;
                            System.out.println("Request : " + req);
                            System.out.print("Reply : ");
                            if(req1[2].contains("localhost")) {
                            	//UDP request
                                DatagramPacket DpSend = new DatagramPacket(p.toBytes(), p.toBytes().length, ip.getLocalHost(), 3000);
                                dsc.send(DpSend);
                                //UDP response          
                                dsc.setSoTimeout(1000);
                                while(true) {
                                	try {
                                		byte[] receive = new byte[p.MAX_LEN];
                                        DpSend = new DatagramPacket(receive, receive.length);
                                        dsc.receive(DpSend);
                                        Packet p1 = Packet.fromBytes(DpSend.getData());
                                        str = new String(p1.getPayload());
                                        System.out.println(str);
                                	}catch(SocketTimeoutException e) {
                                		//System.out.println("Timeout!!!");
                                		break;
                                	}
                                }
                            }else {
                            	str = hlib.post(req1[2]);
                            	System.out.println(str);
                            }
                        }
                        catch (Exception ignored) {
                        }
                        flag=1;
                        break;
                    default:
                        System.out.println("[2]Invalid command.");
                        System.out.println("Use \"httpc help [command]\" for more information about a command.");
                        flag = 1;
                        break;
                }
            } else {
                System.out.println("[3]Invalid command.");
                System.out.println("Use \"httpc help [command]\" for more information about a command.");
                break;
            }
        } while (flag == 0);
    }
}

