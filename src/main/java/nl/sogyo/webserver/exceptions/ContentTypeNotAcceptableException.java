package nl.sogyo.webserver.exceptions;

public class ContentTypeNotAcceptableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ContentTypeNotAcceptableException() {
		super();
	}

	public ContentTypeNotAcceptableException(String string) {
		super(string);
	}
}
