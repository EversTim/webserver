package nl.sogyo.webserver;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseMessage implements Response {

	private HttpStatusCode status;
	private String content;
	private ZonedDateTime requestDate;
	private static final String HTTPVERSION = "HTTP/1.1";
	private static final String SERVERNAME = "TEvers";

	public ResponseMessage(HttpStatusCode status, String content) {
		this.status = status;
		this.content = content;
		this.requestDate = ZonedDateTime.now();
	}

	@Override
	public HttpStatusCode getStatus() {
		return this.status;
	}

	@Override
	public void setStatus(HttpStatusCode status) {
		this.status = status;
	}

	@Override
	public ZonedDateTime getDate() {
		return this.requestDate;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append(HTTPVERSION + " ");
		build.append(this.status.getCode() + " " + this.status.getDescription() + "\n");
		build.append("Date: " + this.getDate().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "\n");
		build.append("Server: " + SERVERNAME + "\n");
		build.append("Connection: close\n");
		build.append("Content-Type: text/html; charset=UTF-8\n");
		if (this.getContent().length() != 0) {
			build.append("Content-Length: " + this.getContent().length() + "\n\n");
			build.append(this.getContent());
		}
		return build.toString();
	}

}
