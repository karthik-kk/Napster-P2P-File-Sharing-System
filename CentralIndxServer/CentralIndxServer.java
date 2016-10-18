/*==============================================================*/
/*       							*/ 
/*	          CENTRAL INDEX SERVER				*/
/*								*/
/*==============================================================*/

//CentralIndxServer
import java.io.*;

//PeerServer
import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class begin
{
	String filename;           //Declaration filename,peerid and ipAddress
	int peerid;
	String ipAddress;
}

class PortListener implements Runnable {

	ServerSocket server;
	Socket connection;
	BufferedReader br = null;
	Boolean flag;
	public String strVal;
	int port;
	static int maxsize = 0;
	static begin[] myIndexArray = new begin[9000];           //ArrayList Initialisation

	public PortListener(int port) {
		this.port = port;
		flag = true;//Initial Idle state
		strVal = "Waiting For PEER Connection";
	}

	/* Beginning of Run Method */	
	public void run() {
		if(port==2001)                                  //Listening For Register on port 2001
		{
			try {
				server = new ServerSocket(2001);
				while (true) {
					connection = server.accept();			
					System.out.println("Connection Received From " +connection.getInetAddress().getHostName()+ " For Registration");    				   				
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
					strVal = (String)in.readObject();
					System.out.println(strVal);
					System.out.println("<====Registered====>\n");
					//Split string "strVal" using Space as Delimeter store as {peerid ,filename} format;
					String[] var;
					var = strVal.split(" ");
					int aInt = Integer.parseInt(var[0]);
					String ipstrtmp = connection.getInetAddress().getHostName();
					/* print substrings */
					for(int x = 1; x < var.length ; x++){

						//  myIndexArray[maxsize].peerid =   .;
						begin myitem = new begin();
						myitem.filename = var[x];                              //Storing Peer ID and Filename in the ArrayList       
						myitem.peerid = aInt  ;
						myitem.ipAddress = ipstrtmp;
						myIndexArray[maxsize] = myitem;
						maxsize++;
					}

					in.close();
					connection.close();   				
				}
			} 

			catch(ClassNotFoundException noclass){                                    //To Handle Exceptions for Data Received in Unsupported/Unknown Formats
				System.err.println("Data Received in Unknown Format");
			}
			catch(IOException ioException){                                           //To Handle Input-Output Exceptions
				ioException.printStackTrace();
			} finally {
			}

		}
		if(port==2002)                                //Listening for Search on port 2002
		{
			try {
				server = new ServerSocket(2002);

				while (true) {
					connection = server.accept();			
					System.out.println("Connection Received From " +connection.getInetAddress().getHostName()+ " For Search");    				   				
					ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
					strVal = (String)in.readObject();
					String retval = "";
					//	Peer-id's separated by space are returned for given file

					for (int idx =0; idx < maxsize ;idx++)                                             //Traversing the ArrayList  
					{                
						if (myIndexArray[idx].filename.equals(strVal))                             //To Compare the filename with the Registered filenames in the ArrayList
						{
							retval = retval + myIndexArray[idx].peerid + "("+myIndexArray[idx].ipAddress +")\n\r ";                  //Returns the list of Peerid's which has the searched file      
						}	
					} 
					if (retval == "") 
					{
						retval = "File Not Found\n";
					} 
					System.out.println(retval);
					System.out.println("<=====Searched=====>\n");

					ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
					out.flush();			
					out.writeObject(retval);                        //Write the List of peer id's to the output stream
					out.flush();			
					in.close();
					out.close();
					connection.close();   				
				}
			} 

			catch(ClassNotFoundException noclass){                                      //To Handle Exceptions for Data Received in Unsupported/Unknown Formats 
				System.err.println("Data Received in Unknown Format");
			}
			catch(IOException ioException){                                             //To Handle Input-Output Exceptions
				ioException.printStackTrace();
			} finally {
			}

		}		
	}
}


/*CentralIndxServer Class Begin*/
public class CentralIndxServer {

	public CentralIndxServer() {
		RegisterRequestThread();                           //RegisterRequest and SearchRequest Threads
		SearchRequestThread();
	}

	public static void main(String[] args) {

		System.out.println("||========================================================================================||");
		System.out.println("||                           PEER-TO-PEER FILE SHARING SYSTEM                             ||");
		System.out.println("||                       ========================================                         ||");
		System.out.println("||========================================================================================||");
		System.out.println("\n <CENTRAL INDEX SERVER IS UP AND RUNNING....>");
		System.out.println(" ============================================\n");


		CentralIndxServer mainFrame = new CentralIndxServer();

	}
	public void RegisterRequestThread()
	{
		Thread rthread = new Thread (new PortListener(2001));                     //Register Request Thread
		rthread.setName("Listen For Register");
		rthread.start();
	}
	public void SearchRequestThread()
	{
		Thread sthread = new Thread (new PortListener(2002));                    //Search Request Thread
		sthread.setName("Listen For Search");
		sthread.start();

	}
}
