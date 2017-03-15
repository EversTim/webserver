package nl.sogyo.webserver;

import nl.sogyo.webserver.exceptions.ContentTypeNotAcceptableException;
import nl.sogyo.webserver.exceptions.IllegalFileAccessException;
import nl.sogyo.webserver.exceptions.MalformedParameterException;
import nl.sogyo.webserver.exceptions.MalformedRequestException;
import nl.sogyo.webserver.exceptions.NoSuchParameterException;
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
			} catch (ContentTypeNotAcceptableException cntae) {
				statusCode = HttpStatusCode.NotAcceptable;
			} catch (NoSuchParameterException nspe) {
				statusCode = HttpStatusCode.BadRequest;
			} catch (RuntimeException re) {
				statusCode = HttpStatusCode.ServerError;
			}
			ResponseMessage message;
			byte[] contents = new byte[0];
			ContentType returnType = request.getContentType();
			if (request.getContentType() != ContentType.NONE) {
				try {
					File requested = this.verifyFile(request.getResourcePath());
					contents = this.setContent(requested);
					returnType = this.checkSetContentType(returnType, requested);
				} catch (IllegalFileAccessException ifae) {
					statusCode = HttpStatusCode.Forbidden;
				} catch (ResourceNotFoundException rnfe) {
					statusCode = HttpStatusCode.NotFound;
				} catch (RuntimeException re) {
					statusCode = HttpStatusCode.ServerError;
				}
			}
			message = new ResponseMessage(statusCode, contents.length, returnType);
			byte[] byteMessage = message.toString().getBytes("UTF-8");
			byte[] output = new byte[byteMessage.length + contents.length];
			System.arraycopy(byteMessage, 0, output, 0, byteMessage.length);
			System.arraycopy(contents, 0, output, byteMessage.length, contents.length);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(output);
			baos.writeTo(this.socket.getOutputStream());
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File verifyFile(String path) throws IllegalFileAccessException, ResourceNotFoundException {
		File requested = new File(path).getAbsoluteFile();
		if (!requested.getAbsoluteFile().exists()) {
			throw new ResourceNotFoundException(requested.getAbsolutePath());
		} else if (path.matches("[A-Z]:.*")) {
			throw new ResourceNotFoundException(requested.getAbsolutePath());
		} else if (!requested.getAbsolutePath().contains(System.getProperty("user.dir"))) {
			throw new IllegalFileAccessException();
		}
		return requested;
	}

	private byte[] setContent(File requested) throws IOException {
		if (requested.isFile()) {
			return Files.readAllBytes(requested.toPath());
		} else {
			StringBuilder build = new StringBuilder();
			build.append("<html>\n");
			build.append("<head>\n");
			build.append("<title>\n");
			build.append("Directory listing of " + requested.getName());
			build.append("</title>\n");
			build.append("</head>\n");
			build.append("<body>\n");
			build.append("<ul>\n");
			for (File s : requested.listFiles()) {
				build.append("<li>");
				build.append(s.getName());
				build.append("</li>");
			}
			build.append("</ul>\n");
			build.append("</body>\n");
			build.append("</html>\n");
			return build.toString().getBytes("UTF-8");
		}
	}

	private ContentType checkSetContentType(ContentType returnType, File responseBody) {
		String filename = responseBody.getName();
		if (filename.matches("\\.jpg$")) {
			returnType = ContentType.IMAGE_JPEG;
		} else if (filename.matches("\\.css$")) {
			returnType = ContentType.TEXT_CSS;
		} else if (filename.endsWith("\\.html?")) {
			returnType = ContentType.TEXT_HTML;
		}
		return returnType;
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