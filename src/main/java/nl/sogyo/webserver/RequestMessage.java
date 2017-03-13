package nl.sogyo.webserver;

import java.util.ArrayList;
import java.util.List;

public class RequestMessage implements Request {

	private HttpMethod httpMethod;
	private String resourcePath;
	private List<HeaderParameter> headerParameters = new ArrayList<>();
	private List<Parameter> parameters = new ArrayList<>();

	public RequestMessage(ArrayList<String> requestString) {
		String[] firstLine = requestString.get(0).split(" ");
		int curLine = 1;
		for (; (curLine < requestString.size()) && (requestString.get(curLine).trim().length() != 0); curLine++) {
			if ((requestString.get(curLine) != null) && (requestString.get(curLine).trim().length() != 0)) {
				HeaderParameter current = new HeaderParameter(requestString.get(curLine));
				this.headerParameters.add(current);
			}
		}
		String method = firstLine[0];
		String[] resourcePathWithParams = firstLine[1].split("\\?");
		if (method.equals("GET")) {
			this.httpMethod = HttpMethod.GET;
		} else if (method.equals("POST")) {
			this.httpMethod = HttpMethod.POST;
		} else {
			// This should really be a custom MalformedRequestException or
			// something like that.
			throw new RuntimeException("Malformed request.");
		}
		// Will not handle spaces in paths gracefully, shouldn't have to (%20).
		this.resourcePath = resourcePathWithParams[0];

		int contentStartLine = curLine + 1;
		String contentLengthStr = null;
		try {
			contentLengthStr = this.getHeaderParameterValue("Content-Length");
		} catch (RuntimeException re) {

		}
		int contentLength = contentLengthStr != null ? Integer.parseInt(contentLengthStr) : -1;
		if (this.httpMethod == HttpMethod.GET) {
			if (resourcePathWithParams.length == 2) {
				String[] params = resourcePathWithParams[1].split("&");
				for (String p : params) {
					this.parameters.add(new Parameter(p));
				}
			}
		} else if ((this.httpMethod == HttpMethod.POST) && (contentLength != -1)) {
			if (requestString.get(contentStartLine).length() != contentLength) {
				throw new RuntimeException("Size mismatch between declared content length and actual content length.");
			}
			String[] params = requestString.get(contentStartLine).split("&");
			for (String p : params) {
				this.parameters.add(new Parameter(p));
			}
		}
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
		List<String> names = new ArrayList<>();
		for (HeaderParameter hp : this.headerParameters) {
			names.add(hp.getName());
		}
		return names;
	}

	@Override
	public String getHeaderParameterValue(String name) {
		for (HeaderParameter hp : this.headerParameters) {
			if (hp.getName().equals(name)) {
				return hp.getValue();
			}
		}
		throw new RuntimeException("No such header parameter found.");
	}

	@Override
	public List<String> getParameterNames() {
		List<String> names = new ArrayList<>();
		for (Parameter hp : this.parameters) {
			names.add(hp.getName());
		}
		return names;
	}

	@Override
	public String getParameterValue(String name) {
		for (Parameter hp : this.parameters) {
			if (hp.getName().equals(name)) {
				return hp.getValue();
			}
		}
		throw new RuntimeException("No such parameter found.");
	}

}
