package nl.sogyo.webserver;

import java.util.ArrayList;
import java.util.List;

public class RequestMessage implements Request {

	private HttpMethod httpMethod;
	private String resourcePath;
	private List<HeaderParameter> headerParameters = new ArrayList<>();

	public RequestMessage(ArrayList<String> requestString) {
		String[] firstLine = requestString.get(0).split(" ");
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
		for (int i = 1; i < requestString.size(); i++) {
			if ((requestString.get(i) != null) && (requestString.get(i).length() != 0)) {
				this.headerParameters.add(new HeaderParameter(requestString.get(i)));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameterValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
