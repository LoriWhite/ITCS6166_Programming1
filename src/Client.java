import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
			InputStreamReader reader = new InputStreamReader(client.getInputStream());
			BufferedReader br = new BufferedReader(reader, 1000);
			OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());
			try
			{
				String output = command + " " + fileName + "\r\n\r\n";
				
				writer.write(output);
				writer.flush();
				
				//prints what the server is outputting
				String line;
				System.out.println("server says:\n");
				while((line = br.readLine()) != null)
				{
					System.out.println(line);
				}
				writer.close();
				reader.close();
				client.close();
			}
			catch (IOException e) 
			{
				System.out.println("Read failed");
				e.printStackTrace();
				System.exit(1);
			}
		 }//exits if the client can not create a input and output for communicating with the server
		 catch (IOException e)
		 {
			System.out.println("Connection failed");
			e.printStackTrace();
	    	System.exit(1);
		 }
	}
}
