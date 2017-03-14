package nl.sogyo.webserver;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ResponseMessage implements Response {

	private HttpStatusCode status;
	private long contentLength;
	private ZonedDateTime requestDate;
	private static final String HTTPVERSION = "HTTP/1.1";
	private static final String SERVERNAME = "TEvers";
	private ContentType type;
	private Object content = null;
	private static final String charset = "charset=UTF-8";

	public ResponseMessage(HttpStatusCode status, long contentLength, ContentType type) {
		this.status = status;
		this.contentLength = contentLength;
		this.requestDate = ZonedDateTime.now();
		this.type = type;
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
	public long getContentLength() {
		return this.contentLength;
	}

	@Override
	public Object getContent() {
		return this.content;
	}

	@Override
	public void setContent(Object content, int contentLength) {
		this.content = content;
		this.contentLength = contentLength;
	}

	@Override
	public ContentType getType() {
		return this.type;
	}

	@Override
	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append(HTTPVERSION + " ");
		build.append(this.status.getCode() + " " + this.status.getDescription() + "\n");
		build.append("Date: " + this.getDate().format(DateTimeFormatter.RFC_1123_DATE_TIME) + "\n");
		build.append("Server: " + SERVERNAME + "\n");
		build.append("Connection: close\n");
		if (this.getType() != ContentType.NONE) {
			build.append("Content-Type: " + this.getType().getType());
			if (this.getType().getGeneralType().equals("text")) {
				build.append("; " + charset);
			}
			build.append("\n");
			build.append("Content-Length: " + this.getContentLength());
			build.append("\n");
			build.append("\n");
		}
		return build.toString();
	}

}
