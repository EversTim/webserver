package nl.sogyo.webserver;

import java.util.List;

public interface Request {
	HttpMethod getHTTPMethod();

	String getResourcePath();

	List<String> getHeaderParameterNames();

	String getHeaderParameterValue(String name);

	List<String> getParameterNames();

	String getParameterValue(String name);

	ContentType getContentType();
}