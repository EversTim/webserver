package nl.sogyo.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
	private Socket socket;

	public ConnectionHandler(Socket toHandle) {
		this.socket = toHandle;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			HttpRequest request;
			try {
				request = new HttpRequest(reader);
			} catch (IOException ie) {
				return;
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			writer.write(
					String.format("You did an HTTP %1$S request and you requested the following resource: %2$s.\r\n",
							request.getHTTPMethod().toString(), request.getResourcePath()));
			writer.flush();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String... args) {
		try (ServerSocket socket = new ServerSocket(9090)) {
			while (true) {
				Socket newConnection = socket.accept();
				Thread t = new Thread(new ConnectionHandler(newConnection));
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}