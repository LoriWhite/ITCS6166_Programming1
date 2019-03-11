import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable
{
	Socket client;
	final String host;
	final int portNumber;
	final String command;
	final String fileName; 
	
	public static void main(String [] args) 
	{
		if(args.length == 4)
		{
			try 
			{
				String h1 = args[0];
				int p = Integer.parseInt(args[1]);
				String c = args[2];
				String f = args[3];
				Thread t1 = new Thread(new Client(h1, p, c, f));
				t1.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.exit(0);
			}
		}
		else
		{
			System.out.println("Incorrect number of parameters.");
		}
	}
	
	Client(String inHost, int inPort, String inCommand, String inFile)
	{
		host = inHost;
		portNumber = inPort;
		command = inCommand;
		fileName = inFile;
		System.out.println("Creating socket to '" + host + "' on port " + portNumber);
	    try 
	    {
			this.client =  new Socket(host, portNumber);
		} 
	    catch (UnknownHostException e) 
		{
			System.out.println("Unknown host: localhost");
			System.exit(0);
		}//exits when there is not any input or output from the server
		catch (IOException e) 
		{
			System.out.println("No I/O");
			System.exit(0);
		}
	}
	
	public void run()
	{
		try
		{
			//creates the input and output communication  
			DataInputStream reader = new DataInputStream(client.getInputStream());
			DataOutputStream writer = new DataOutputStream(client.getOutputStream());
			try
			{
				writer.writeUTF(command + " " + fileName);
				//prints what the server is outputting
				System.out.println("server says:" + reader.readUTF());
			}
			catch (IOException e) 
			{
				System.out.println("Read failed");
				System.exit(1);
			}
		 }//exits if the client can not create a input and output for communicating with the server
		 catch (IOException e)
		 {
			System.out.println("Read failed");
	    	System.exit(1);
		 }
	}
}
