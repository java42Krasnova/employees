package telran.net;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

import telran.net.dto.*;

public class TcpClientServer implements Runnable {
	Socket socket;
	ObjectInputStream reader;
	ObjectOutputStream writer;
	ApplProtocol protocol;
	static AtomicBoolean shutdown = new AtomicBoolean(false);
	
	public TcpClientServer(Socket socket, ApplProtocol protocol) throws Exception {
		this.socket = socket;
		//this.socket.setSoTimeout(TcpServer.timeout);
		// socket.setSoTimeout(1000); //if socket is in the idle mode after 1 sec.
		// there will be SocketTimeoutException
		reader = new ObjectInputStream(socket.getInputStream());
		writer = new ObjectOutputStream(socket.getOutputStream());
		this.protocol = protocol;
		
	}

	@Override
	public void run() {
		try {
			while (true) {
				Request request = (Request) reader.readObject();
				Response response = protocol.getResponse(request);
				writer.writeObject(response);
			}
		} catch (SocketTimeoutException e) {
		//	shutdown.set(true);
		} catch (EOFException e) {
			try {
				socket.close();
			} catch (IOException e1) {

				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}