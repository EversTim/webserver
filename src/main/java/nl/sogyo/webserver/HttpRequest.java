package nl.sogyo.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class HttpRequest implements Request {

	private HttpMethod httpMethod;
	private String resourcePath;

	public HttpRequest(BufferedReader request) throws IOException {
		String[] firstLine = request.readLine().split(" ");
		String method = firstLine[0];
		if (method.equals("GET")) {
			this.httpMethod = HttpMethod.GET;
		} else if (method.equals("POST")) {
			this.httpMethod = HttpMethod.POST;
		} else {
			// This should really be a custom MalformedRequestException or
			// something like that.
			throw new RuntimeException("Malformed request.");
		}
		// Will not handle spaces in paths gracefully.
		this.resourcePath = firstLine[1];
	}

	@Override
	public HttpMethod getHTTPMethod() {
		return this.httpMethod;
	}

	@Override
	public String getResourcePath() {
		return this.resourcePath;
	}

	@Override
	public List<String> getHeaderParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeaderParameterValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameterValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
