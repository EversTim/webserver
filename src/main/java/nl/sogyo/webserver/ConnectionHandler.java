package nl.sogyo.webserver;

import nl.sogyo.webserver.exceptions.ContentTypeNotAcceptableException;
import nl.sogyo.webserver.exceptions.IllegalFileAccessException;
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
				if (request.getContentType() != ContentType.NONE) {
					File resource = new File(request.getResourcePath());
					if (!resource.getAbsolutePath().contains(System.getProperty("user.dir"))) {
						throw new IllegalFileAccessException();
					}
					if (!resource.getAbsoluteFile().exists()) {
						throw new ResourceNotFoundException(resource.getAbsolutePath());
					}
				}
			} catch (MalformedParameterException mpe) {
				statusCode = HttpStatusCode.BadRequest;
			} catch (MalformedRequestException mre) {
				statusCode = HttpStatusCode.BadRequest;
			} catch (ResourceNotFoundException rnfe) {
				statusCode = HttpStatusCode.NotFound;
				System.out.println(rnfe.getMessage());
			} catch (ContentTypeNotAcceptableException cntae) {
				statusCode = HttpStatusCode.NotAcceptable;
			} catch (IllegalFileAccessException ifae) {
				statusCode = HttpStatusCode.Forbidden;
			} catch (RuntimeException re) {
				statusCode = HttpStatusCode.ServerError;
			}
			ResponseMessage message;
			if (statusCode == HttpStatusCode.OK) {
				byte[] contents = new byte[0];
				ContentType returnType = request.getContentType();
				if (request.getContentType() != ContentType.NONE) {
					File responseBody = new File(request.getResourcePath()).getAbsoluteFile();
					contents = Files.readAllBytes(responseBody.toPath());
					if (responseBody.getAbsolutePath().endsWith("jpg")) {
						returnType = ContentType.IMAGE_JPEG;
					}
				}
				message = new ResponseMessage(statusCode, contents.length, returnType);
				byte[] byteMessage = message.toString().getBytes("UTF-8");
				byte[] output = new byte[byteMessage.length + contents.length];
				System.arraycopy(byteMessage, 0, output, 0, byteMessage.length);
				System.arraycopy(contents, 0, output, byteMessage.length, contents.length);
				this.socket.getOutputStream().write(output);
			} else {
				message = new ResponseMessage(statusCode, 0, ContentType.NONE);
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
				writer.write(message.toString());
				writer.close();
			}
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
		return incomingMessage;
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