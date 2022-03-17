package telran.net;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
public class TcpServer implements Runnable {
private static final int N_THTEADS = 5;
private int port;
private ApplProtocol protocol;
private ServerSocket serverSocket;
int nThreads = N_THTEADS;
public void setnThreads (int nThreads) {
	this.nThreads = nThreads;
}
public TcpServer(int port, ApplProtocol protocol) throws Exception{
	this.port = port;
	this.protocol = protocol;
	serverSocket = new ServerSocket(port);
	
}
	@Override
	public void run() {
		System.out.println("Server is listening on the port " + port);
		ExecutorService executor = Executors.newFixedThreadPool(nThreads, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread res = new Thread(r);
				res.setDaemon(true);
				return res;
			}
		});
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				TcpClientServer client = new TcpClientServer(socket, protocol);
				//Thread threadClient = new Thread(client);
				//threadClient.start();
				executor.execute(client);
			} catch (Exception e) {
				
				e.printStackTrace();
				break;
			}
		}

	}

}