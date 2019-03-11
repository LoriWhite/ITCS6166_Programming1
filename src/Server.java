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

public class Server 
{
	private final int port;
	private ServerSocket serverSocket;
	private ThreadPoolExecutor threadpool;
	Object lock;
	
	public Server(int port) 
	{
		this.port = port;
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
	
	public void listen() 
	{
		try 
		{
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
	
	public static void process(Socket socket) 
	{
		try 
		{
			InputStreamReader input = new InputStreamReader(socket.getInputStream());
			BufferedReader reader = new BufferedReader(input, 10000);
			OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());
			//BufferedWriter writer = new BufferedWriter(output);
			String line = reader.readLine();
			System.out.println(line);
			
			if(line != null && line != "") 
			{
				String[] splits = line.split(" ");
				String action = splits[0];
				String filename = splits[1];
				
				if(action.equals("GET")) 
				{
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
				else if(action.equals("PUT")) 
				{
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
			}
			
	        output.close();
	        reader.close();
	        socket.close();
		}
		catch(IOException e) 
		{
			return;
		}
	}
	
	public void run() 
	{
		System.out.println(port);
		listen();
	}
	
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
