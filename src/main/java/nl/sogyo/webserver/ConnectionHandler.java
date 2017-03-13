package nl.sogyo.webserver;

import java.util.List;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler implements Runnable {
	private Socket socket;

	public ConnectionHandler(Socket toHandle) {
		this.socket = toHandle;
	}

	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			ArrayList<String> incomingMessage = new ArrayList<>();
			String line = reader.readLine();
			while (!line.isEmpty()) {
				incomingMessage.add(line);
				line = reader.readLine();
			}
			incomingMessage.add("");
			StringBuilder body = new StringBuilder();
			while (reader.ready()) {
				int read = reader.read();
				body.append((char) read);
			}
			incomingMessage.add(body.toString());
			RequestMessage request = new RequestMessage(incomingMessage);
			String responseMessage = generateResponseMessage(request);
			ResponseMessage message = new ResponseMessage(HttpStatusCode.OK, responseMessage);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			writer.write(message.toString());
			writer.flush();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String generateResponseMessage(RequestMessage request) {
		StringBuilder responseMessage = new StringBuilder();
		responseMessage.append("<html>\n<body>\n");
		responseMessage.append(String.format(
				"You did an HTTP %1$S request.<br />\n You requested the following resource: %2$s.<br />\n",
				request.getHTTPMethod().toString(), request.getResourcePath()));
		responseMessage.append("<br />\n");
		List<String> headerParamNames = request.getHeaderParameterNames();
		if (headerParamNames.size() != 0) {
			responseMessage.append("The following header parameters were passed:<br />\n");
			for (String name : headerParamNames) {
				responseMessage.append(name + ": " + request.getHeaderParameterValue(name) + "<br />\n");
			}
			responseMessage.append("<br />\n");
		}
		List<String> paramName = request.getParameterNames();
		if (paramName.size() != 0) {
			responseMessage.append("The following parameters were passed:<br />\n");
			for (String name : paramName) {
				responseMessage.append(name + ": " + request.getParameterValue(name) + "<br />\n");
			}
		}

		responseMessage.append("</body>\n</html>\n");
		return responseMessage.toString();
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