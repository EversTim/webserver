package nl.sogyo.webserver;

import java.util.List;

import nl.sogyo.webserver.exceptions.NoSuchParameterException;

public interface Request {
	HttpMethod getHTTPMethod();

	String getResourcePath();

	List<String> getHeaderParameterNames();

	String getHeaderParameterValue(String name) throws NoSuchParameterException;

	List<String> getParameterNames();

	String getParameterValue(String name) throws NoSuchParameterException;

	ContentType getContentType();
}