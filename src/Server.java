import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Takes a command line argument that specifies the port number and uses it to listen for incoming connection requests.
 * 
 * @author Matthew Goodman, Lori White
 * @version 3/11/2019
 */
public class Server 
{
	private final int port;
	private ServerSocket serverSocket;
	private ThreadPoolExecutor threadpool;
	Object lock;
	
	/**
	 * A Server object.
	 * 
	 * @param inPort - the port number
	 */
	public Server(int inPort) 
	{
		this.port = inPort;
		try 
		{
			lock = new Object();
			serverSocket = new ServerSocket(port);
			threadpool = (ThreadPoolExecutor)Executors.newCachedThreadPool();
			//serverSocket.setSoTimeout(100);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 *  Listens for client requests.
	 */
	public void listen() 
	{
		try 
		{
			System.out.println("Server Online!");
			for(;;) 
			{
				Socket socket = serverSocket.accept();
				if(socket != null) 
				{
					threadpool.execute(()->{process(socket);});
				}
			}
		}
		catch(IOException e) 
		{
			return;
		}
	}
	
	/**
	 * Processes the Client's request.
	 * 
	 * @param socket - incoming client
	 */
	public static void process(Socket socket) 
	{
		try 
		{
			InputStreamReader input = new InputStreamReader(socket.getInputStream());
			BufferedReader reader = new BufferedReader(input, 10000);
			OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
			//BufferedWriter writer = new BufferedWriter(output);
			String line = reader.readLine();
			
			//System.out.println(line);
			
			if(line != null && line != "") 
			{
				String[] splits = line.split(" ");
				String action = splits[0];
				String filename = splits[1];
				
				if(action.equalsIgnoreCase("GET")) 
				{
					System.out.println("Processing GET request from: " + socket.getInetAddress().toString());
					
					File file = new File("./" + filename);
					if(file.exists()) 
					{
						output.write("HTTP/1.1 200 OK\r\n\r\n");
						//FileInputStream fis = new FileInputStream(file);
						FileReader fr = new FileReader(file);
						char[] buffer = new char[1024];
						while(fr.read(buffer) > 0) 
						{
							output.write(buffer);
						}
						fr.close();
					} 
					else 
					{
						output.write("HTTP/1.1 404 Not Found\r\n\r\n");
					}
				} 
				else if(action.equalsIgnoreCase("PUT")) 
				{
					System.out.println("Processing PUT request from: " + socket.getInetAddress().toString());
					
					File file = new File("./" + filename);
					FileWriter fw = new FileWriter(file);
					
					do 
					{
						line = reader.readLine();
					} 
					while(!line.isEmpty());
					
					char[] buffer = new char[1024];
					while(reader.ready() && reader.read(buffer) > 0) 
					{
						fw.write(buffer);
					}
					fw.close();
					output.write("HTTP/1.1 200 OK File Created\r\n\r\n");
				}
				else {
					System.out.println("Unknown request from: " + socket.getInetAddress().toString());
				}
			}
			
	        output.close();
	        reader.close();
	        socket.close();
		}
		catch(IOException e) 
		{
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * Runs the Server.
	 */
	public void run() 
	{
		//System.out.println(port);
		listen();
	}
	
	/**
	 * Closes the Server.
	 */
	public void dispose() 
	{
		try 
		{
			serverSocket.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) 
	{
		if(args.length != 1) 
		{
			System.out.println("Not enough arguments!");
			return;
		}
		int port = 0;
		try 
		{
			port = Integer.parseInt(args[0]);
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
		Server server = new Server(port);
		server.run();
		server.dispose();
	}
}
