package nl.sogyo.webserver;

public enum HttpStatusCode {
	OK(200, "OK"), BadRequest(400, "Bad Request"), NotFound(404, "Not Found"), ServerError(500, "Server Error");

	private int code;
	private String description;

	private HttpStatusCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public int getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}
}