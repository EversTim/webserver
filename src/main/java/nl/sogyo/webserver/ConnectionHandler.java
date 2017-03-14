package nl.sogyo.webserver;

import nl.sogyo.webserver.exceptions.ContentTypeNotAcceptableException;
import nl.sogyo.webserver.exceptions.MalformedParameterException;
import nl.sogyo.webserver.exceptions.MalformedRequestException;
import nl.sogyo.webserver.exceptions.ResourceNotFoundException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
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
			RequestMessage request = new RequestMessage();
			HttpStatusCode statusCode = HttpStatusCode.OK;
			try {
				request = new RequestMessage(this.readIncomingMessage(reader));
			} catch (MalformedParameterException mpe) {
				statusCode = HttpStatusCode.BadRequest;
			} catch (MalformedRequestException mre) {
				statusCode = HttpStatusCode.BadRequest;
			} catch (ResourceNotFoundException rnfe) {
				statusCode = HttpStatusCode.NotFound;
			} catch (ContentTypeNotAcceptableException cntae) {
				statusCode = HttpStatusCode.NotAcceptable;
			} catch (RuntimeException re) {
				statusCode = HttpStatusCode.ServerError;
			}
			String responseMessageBody = this.generateResponseMessageBody(request, statusCode,
					request.getContentType());
			// System.out.println(responseMessageBody);
			ResponseMessage message = new ResponseMessage(statusCode, responseMessageBody, request.getContentType());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			writer.write(message.toString());
			writer.flush();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> readIncomingMessage(BufferedReader reader) throws IOException {
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
		for (String s : incomingMessage) {
			System.out.println(s);
		}
		return incomingMessage;
	}

	public String generateResponseMessageBody(RequestMessage request, HttpStatusCode statusCode,
			ContentType contentType) {
		StringBuilder responseMessage = new StringBuilder();
		File file = new File(request.getResourcePath());
		if (statusCode == HttpStatusCode.OK) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
				while (reader.ready()) {
					int read = reader.read();
					responseMessage.append((char) read);
				}
			} catch (FileNotFoundException fnfe) {
				// Should never happen, statusCode will be 404.
				// Log in case it does.
				System.out.println("File was not found after check for 404!");
			} catch (IOException ie) {
				// THIS SPACE INTENTIONALLY LEFT BLANK
			}
		} else {
			responseMessage.append("Something went wrong! " + statusCode.getCode() + " " + statusCode.getDescription());
		}
		return responseMessage.toString();
	}

	public static void main(String... args) {
		File directory = new File("var/www").getAbsoluteFile();
		System.setProperty("user.dir", directory.getAbsolutePath());
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