import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private final int port;
	private ServerSocket serverSocket;
	private Thread thread;
	Object lock;
	
	public Server(int port) {
		this.port = port;
		try {
			lock = new Object();
			serverSocket = new ServerSocket(port);
			//serverSocket.setSoTimeout(100);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void listen() {
		try {
			for(;;) {
				Socket socket = serverSocket.accept();
				if(socket != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String line;
					do {
						line = reader.readLine();
						System.out.println(line);
					} while(line != null && line.length() != 0);
					socket.getOutputStream().write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
				}
			}
		}catch(IOException e) {return;}
	}
	
	public void run() {
		System.out.println(port);
		listen();
	}
	
	public static void main(String [] args) {
		if(args.length != 1) {
			System.out.println("Not enough argumetns!");
			return;
		}
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		}catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		Server server = new Server(port);
		server.run();
	}
}
