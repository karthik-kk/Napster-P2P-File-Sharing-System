import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;


class Perf_Test{
String regmsg;
String CIS_ip = "localhost";
ObjectOutputStream op;
Socket rqstSoc;


public static void main(String[] args) {

System.out.println("Enter the peer id");
Scanner in = new Scanner(System.in);
    	 String regmsg = in.nextLine();
String a ="null";

File folder = new File("/Users/karthikk/Desktop/P2P_File_Sharing_Project/TestCase_Performance_Eval/Dummy_Files/"); //Enter PATH here to REGISTER 1000 Files
File[] listOfFiles = folder.listFiles();
long regStartTime = new Date().getTime();
for (int i = 0; i < listOfFiles.length; i++)
{
a=listOfFiles[i].getName();
Perf_Test t = new Perf_Test();
t.register(regmsg,a);
}
long regEndTime = new Date().getTime();
long diff = regEndTime - regStartTime;
System.out.println("The Time taken to Register:"+diff);

}
public void register(String re,String name)
    {
   		try{
regmsg = re.concat(" "+name);
//1. creating a socket to connect to the server
   			rqstSoc = new Socket(CIS_ip,2001);
			System.out.println("Connected to Register on CentralIndxServer on prt 2001");
			//2. get Input and Output streams
			op= new ObjectOutputStream(rqstSoc.getOutputStream());
			op.flush();			
			op.writeObject(regmsg);
			op.flush();
 		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an invalid host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				op.close();
				rqstSoc.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
   	
    }

}

