package nl.sogyo.webserver;

import java.util.ArrayList;
import java.util.List;

import nl.sogyo.webserver.exceptions.ContentTypeNotAcceptableException;
import nl.sogyo.webserver.exceptions.MalformedParameterException;
import nl.sogyo.webserver.exceptions.MalformedRequestException;
import nl.sogyo.webserver.exceptions.NoSuchParameterException;

public class RequestMessage implements Request {

	private HttpMethod httpMethod;
	private String resourcePath;
	private ContentType contentType;
	private List<HeaderParameter> headerParameters = new ArrayList<>();
	private List<Parameter> parameters = new ArrayList<>();
	private int currentReadingLine = 1;

	RequestMessage() {
		this.contentType = ContentType.NONE;
	}

	public RequestMessage(ArrayList<String> requestString) throws ContentTypeNotAcceptableException,
			MalformedParameterException, MalformedRequestException, NoSuchParameterException {
		String[] firstLine = requestString.get(0).split(" ");
		String method = firstLine[0];
		String[] resourcePathWithParams = firstLine[1].split("\\?");
		if (method.equals("GET")) {
			this.httpMethod = HttpMethod.GET;
		} else if (method.equals("POST")) {
			this.httpMethod = HttpMethod.POST;
		} else {
			throw new MalformedRequestException("Malformed request, neither GET or POST.");
		}
		// Will not handle spaces in paths gracefully, shouldn't have to (%20).
		this.resourcePath = resourcePathWithParams[0].substring(1, resourcePathWithParams[0].length());
		if (this.resourcePath.equals("")) {
			this.resourcePath = "index.html";
		}

		this.headerParameters = this.extractHeaderParameters(requestString);
		this.contentType = this.determineContentType();

		this.currentReadingLine++;
		String contentLengthStr = null;
		if (this.contentType != ContentType.NONE) {
			try {
				contentLengthStr = this.getHeaderParameterValue("Content-Length");
			} catch (NoSuchParameterException nspe) {
				// THIS SPACE INTENTIONALLY LEFT BLANK
				// Will simply not read any content.
			}
		}
		int contentLength = contentLengthStr != null ? Integer.parseInt(contentLengthStr) : -1;
		this.parameters = this.extractParameters(requestString, resourcePathWithParams, contentLength);
	}

	private List<Parameter> extractParameters(ArrayList<String> requestString, String[] resourcePathWithParams,
			int contentLength) throws MalformedParameterException {
		String[] params = new String[0];
		if (this.httpMethod == HttpMethod.GET) {
			if (resourcePathWithParams.length == 2) {
				params = resourcePathWithParams[1].split("&");

			}
		} else if ((this.httpMethod == HttpMethod.POST) && (contentLength > 0)) {
			if (requestString.get(this.currentReadingLine).length() != contentLength) {
				throw new MalformedParameterException(
						"Size mismatch between declared content length and actual content length.");
			}
			params = requestString.get(this.currentReadingLine).split("&");
		}
		List<Parameter> paramList = new ArrayList<>();
		for (String p : params) {
			Parameter current = new Parameter(p);
			paramList.add(current);
		}
		return paramList;
	}

	private List<HeaderParameter> extractHeaderParameters(ArrayList<String> requestString) {
		List<HeaderParameter> hParams = new ArrayList<>();
		for (; (this.currentReadingLine < requestString.size())
				&& (requestString.get(this.currentReadingLine).trim().length() != 0); this.currentReadingLine++) {
			if (requestString.get(this.currentReadingLine) != null) {
				HeaderParameter current = new HeaderParameter(requestString.get(this.currentReadingLine));
				hParams.add(current);
			}
		}
		return hParams;
	}

	private ContentType determineContentType() throws ContentTypeNotAcceptableException, NoSuchParameterException {
		if (this.getHeaderParameterNames().contains("Accept")) {
			String acceptParam = this.getHeaderParameterValue("Accept");
			if (acceptParam.contains("text/html")) {
				return ContentType.TEXT_HTML;
			} else if (acceptParam.contains("text/css")) {
				return ContentType.TEXT_CSS;
			} else if (acceptParam.contains("image/jpeg")) {
				return ContentType.IMAGE_JPEG;
			} else if (acceptParam.contains("image/*")) {
				return ContentType.IMAGE_JPEG;
			} else if (acceptParam.contains("*/*")) {
				return ContentType.TEXT_HTML;
			} else {
				throw new ContentTypeNotAcceptableException("Unknown content type.");
			}
		} else {
			return ContentType.NONE;
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
	public String getHeaderParameterValue(String name) throws NoSuchParameterException {
		for (HeaderParameter hp : this.headerParameters) {
			if (hp.getName().equals(name)) {
				return hp.getValue();
			}
		}
		throw new NoSuchParameterException("No header parameter \"" + name + "\" found.");
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
	public String getParameterValue(String name) throws NoSuchParameterException {
		for (Parameter hp : this.parameters) {
			if (hp.getName().equals(name)) {
				return hp.getValue();
			}
		}
		throw new NoSuchParameterException("No parameter \"" + name + "\" found.");
	}

	@Override
	public ContentType getContentType() {
		return this.contentType;
	}
}
