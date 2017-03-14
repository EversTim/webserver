package nl.sogyo.webserver;

import java.time.ZonedDateTime;

public interface Response {
	HttpStatusCode getStatus();

	void setStatus(HttpStatusCode status);

	ZonedDateTime getDate();

	Object getContent();

	void setContent(Object content, int contentLength);

	long getContentLength();

	ContentType getType();
}