package telran.net;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class TcpServer implements Runnable {
private static final int DEFAULT_THREADS_POOL_CAPACITY = 3;
private int port;
private ApplProtocol protocol;
private ServerSocket serverSocket;
private ExecutorService executor;
static int timeout;//changed

public TcpServer(int port, ApplProtocol protocol, int nThreads/*, int timeout*/) throws Exception{
	this.port = port;
	this.protocol = protocol;
	serverSocket = new ServerSocket(port);
	executor = Executors.newFixedThreadPool(nThreads);
//	this.timeout = timeout;//changed
}
public TcpServer(int port, ApplProtocol protocol/*,int timeout*/) throws Exception{
	this(port, protocol, DEFAULT_THREADS_POOL_CAPACITY/*, timeout*/);
	
}

	@Override
	public void run() {
		System.out.println("Server is listening on the port " + port);
		while (!TcpClientServer.shutdown.get()) {
			try {
				Socket socket = serverSocket.accept();
				TcpClientServer client = new TcpClientServer(socket, protocol);
				
				executor.execute(client);
			} catch(SocketTimeoutException e) {
				//?
			}
			catch (Exception e) {
				
				e.printStackTrace();
				break;
			}
		}

	}
	public void shutdown(int timeout) throws SocketException {
//		serverSocket.setSoTimeout(timeout);//changed
//		TcpClientServer.shutdown.set(true);
		//TODO - solution of a graceful shutdown
		//What is a graceful server shutdown
		//1. No receive new clients
		//2. Running getResponse should be performed
		//3. After SocketTimeoutException exiting from the loop for each client (to trigger SocketTimeoutException
		// you should apply method setSoTimeout for the client sockets with the given timeout)
	}

}