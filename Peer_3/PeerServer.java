/*=================================================================*/
/*								   */
/*			PEER SERVER - 3 			   */
/*							           */
/*								   */
/*=================================================================*/
import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Scanner;

//PeerServer
class PortListenerSend implements Runnable {

	int port;
	public String strVal;
	Boolean flag;                            //declarations
	ServerSocket server;
	Socket connection;
	BufferedReader br = null;

	public PortListenerSend(int port) {
		this.port = port;
		flag = true;//Initial Idle state
		strVal = "Waiting For PEER Connection";
	}
	/* Beginning of Run Method */
	public void run() {
		try {
			server = new ServerSocket(port);

			while (true) {                                                                       //Listen for Download request
				connection = server.accept();			
				System.out.println("Connection Received From " + connection.getInetAddress().getHostName()+" For Download\n");    				   				
				ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
				strVal = (String)in.readObject();

				ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();		

				String str="";

				try
				{
					FileReader fr = new FileReader(strVal);                 //Reads the filename into Filereader
					BufferedReader br = new BufferedReader(fr);		
					String value=new String();
					while((value=br.readLine())!=null)                //Appending the content read from the BufferedReader object until it is null and stores it in str
						str=str+value+"\r\n";                       
					br.close();
					fr.close();
				} catch(Exception e){
					System.out.println("Cannot Open File");
				}

				out.writeObject(str);
				out.flush();
				in.close();
				connection.close();   				
			}
		} 

		catch(ClassNotFoundException noclass){                                            //To Handle Exception for Data Received in Unsupported or Unknown Formats
			System.err.println("Data Received in Unknown Format");
		}
		catch(IOException ioException){                                                   //To Handle Input-Output Exceptions
			ioException.printStackTrace();
		} finally {
		}
	}
}

/* PeerServer Class Begin */
public class PeerServer {

	//public String CIS_ip = "10.0.0.13";       //============>IP-address of the CentralIndxServer has to be specified here
	public String CIS_ip = "localhost";       //============>IP-address of the CentralIndxServer has to be specified here
	public String Clientid = "1001";
	String regmessage,searchfilename;
	ObjectOutputStream out;
	Socket requestSocket;

	public PeerServer() {


		try
		{
			FileReader fr = new FileReader("indxip.txt");//read the filename in to filereader object    
			String val1=new String();
			BufferedReader br = new BufferedReader(fr);	
			val1 = br.readLine();
			System.out.println("IndexServer IP is:" + val1);
			CIS_ip = val1;
			br.close();
			fr.close();
		} catch(Exception e){
			System.out.println("Could not read indexserver ip from indxip.txt");
		}






		/* START - Commented Out To Test for Register Function */
		/*   
		     System.out.println("Enter the string in format: 4digit id and file names saparated by space");
		     Scanner in = new Scanner(System.in);
		     regmessage = in.nextLine();

		//	RegisterWithIServer();
		//collect client id from input string
		String[] temp;
		temp = regmessage.split(" ");
		int PearPort  = Integer.parseInt(temp[0]);

		AttendFileDownloadRequest(PearPort);
		 */
		/* END - Commented Out To Test for Register Function */

		System.out.println("||========================================================================================||");
		System.out.println("||                           PEER-TO-PEER FILE SHARING SYSTEM                             ||");
		System.out.println("||                       ========================================                         ||");
		System.out.println("||                                       MENU:                                            ||");
		System.out.println("||========================================================================================||");

		while (true){

			//  System.out.println("\n");
			System.out.println("============================================================================================\n");
			System.out.println("Enter The Option :\n==================\n1. Registering the File \n \n2. Searching On CentralIndxServer \n \n3. Downloading From Peer Server \n \n4. Exit\n");	
			Scanner in = new Scanner(System.in);
			regmessage = in.nextLine();
			if (regmessage.equals("1")){

				System.out.println("Enter the String in Format: 4Digit id and File Names separated by Space");
				//	Scanner in = new Scanner(System.in);  
				regmessage = in.nextLine();
				//To Collect peer/client id from input string
				String[] val;
				val = regmessage.split(" ");                        //Split the peerid and filename separated with a space
				int PearPort  = Integer.parseInt(val[0]);
				// START - For FILE CHECKING in the Given PATH	
				/*	
					String fcheck = (val[1]);
					File folder = new File("/Users/karthikk/Desktop/P2P_File_Sharing_Project/Peer_1/");
					File[] listAllFiles = folder.listFiles();
					int temp = 0;
					for (int i=0; i<listAllFiles.length; i++)
					{
					if((listAllFiles[i].getName()).equals(fcheck)) {
					temp = 1;
				 */			
				RegisterWithIServer();                          //Register Method call
				AttendFileDownloadRequest(PearPort);
				/*		}
					}
					if (temp == 0){
					System.out.println("File Not Found !!");
					}
				 */		

				// END - For FILE CHECKING in the Given PATH	
			}		
			if (regmessage.equals("2")){
				SearchWithIServer();                            //Search Method call
			}
			if (regmessage.equals("3")){
				DownloadFromPeerServer();                       //Download Method call 
			}
			if (regmessage.equals("4")){
				System.out.println("Exiting.");
				System.exit(0);   		
			}

			//	if (!(regmessage.equals("1")) || (regmessage.equals("2")) || (regmessage.equals("3")) || (regmessage.equals("4")))        //To Test for Invalid Conditions   
			//	{
			//		System.out.println("Please Enter a Valid Option");
			//	}	
		}
	}

	/* Main Method Begin */
	public static void main(String[] args) {

		PeerServer psFrame = new PeerServer();

	}
	public void RegisterWithIServer()                             //Register with CentralIndxServer Method
	{
		try{
			//1. Creating a socket to connect to the server
			requestSocket = new Socket(CIS_ip, 2001);
			System.out.println("\nConnected to Register on CentralIndxServer on port 2001\n");
			//2. To Get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(regmessage);
			out.flush();
			System.out.println("Registered Successfully!!\n");
		}
		catch(UnknownHostException unknownHost){                                             //To Handle Unknown Host Exception
			System.err.println("Cannot Connect to an Unknown Host!");
		}
		catch(IOException ioException){                                                      //To Handle Input-Output Exception
			ioException.printStackTrace();
		} 
		finally{
			//4: Closing connection
			try{
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}

	}
	public void SearchWithIServer()                              //Search on the CentralIndexServer Method
	{
		try{
			System.out.println("Enter the File Name to Search");
			Scanner in1 = new Scanner(System.in);                                        //Takes Input from the Peer to search the desired file
			searchfilename = in1.nextLine();

			//1. Creating a socket to connect to the Index server
			requestSocket = new Socket(CIS_ip, 2002);
			System.out.println("\nConnected to Search on CentralIndxServer on port 2002\n");
			//2. To Get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(searchfilename);                                            //Writes the Search Filename to the Output Stream
			out.flush();
			ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
			String strVal = (String)in.readObject();
			//  For File Not Found Print Condition 
			if  (strVal.equals("File Not Found\n")) {

				System.out.println("FILE Does Not Exist !!\n");
			}
			else {
				System.out.println( "File:'"+searchfilename+ "' found at peers:"+strVal+"\n");     
			}		

		}
		catch(UnknownHostException unknownHost){                                           //To Handle Unknown Host Exception
			System.err.println("Cannot Connect to an Unknown Host!");
		}
		catch(IOException ioException){                                                    //To Handle Input-Output Exception
			ioException.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

	public void writetoFile(String s)
	{
		try
		{  
			//To Append String s to Existing File
			String fname = searchfilename;
			FileWriter fw = new FileWriter(fname,true);
			fw.write(s);                                      //Write to file, the contents
			fw.close();

		} catch(Exception e){
			System.out.println("");
			//	System.out.println("Cannot Open File");     // To Mask Print on Console
		}

	}

	public void DownloadFromPeerServer()                            //Download Function Method 
	{

		System.out.println("Enter Peer id:");                       
		Scanner in1 = new Scanner(System.in);                       //Takes from the user the 4Digit Peer ID as input 
		String peerid = in1.nextLine();

		System.out.println("Enter pear IP Address to download file:");
		String ipadrs = in1.nextLine();
		System.out.println("Enter the File Name to be Downloaded:");      
		searchfilename = in1.nextLine();                              //Takes from user the desired filename to be downloaded

		/* START - Commented Out to Test for Direct Download */
		/*
		   String searchfilename;
		   System.out.println("Enter the Filename to be Downloaded");
		   Scanner in1 = new Scanner(System.in);
		   searchfilename=in1.nextLine();



		   System.out.println("Enter peer id to download file:'"+searchfilename +"'");
		// Scanner in1 = new Scanner(System.in);
		String peerid = in1.nextLine();
		 */			
		/* END - Commented Out to Test for Direct Download */

		int peerid1 = Integer.parseInt(peerid);
		try{

			//1. Creating a socket to connect to the Index server
			requestSocket = new Socket(ipadrs, peerid1);
			System.out.println("\nConnected to peerid : "+peerid1+"\n");
			//2. To Get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();			
			out.writeObject(searchfilename);
			out.flush();
			ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
			String strVal = (String)in.readObject();
			System.out.println( searchfilename+": Downloaded\n");
			writetoFile(strVal);
		}
		catch(UnknownHostException unknownHost){                                             //To Handle Unknown Host Exception
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){                                                      //To Handle Input-Output Exception

			System.err.println("FILE not Found at the Following PEER !!");      
			System.err.println("Please enter a valid PEER ID!");      // To Avoid StackTrace Print on Console and Inform User
			DownloadFromPeerServer();                    // Calling Download Function Again to enable user to enter valid Filename and Port Number
			//	ioException.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				//	in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	public void AttendFileDownloadRequest(int peerid)                                //FileDownload Request Thread   
	{
		Thread dthread = new Thread (new PortListenerSend(peerid));
		dthread.setName("AttendFileDownloadRequest");
		dthread.start();
	}
}
