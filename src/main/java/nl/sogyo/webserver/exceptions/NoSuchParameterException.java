package nl.sogyo.webserver.exceptions;

public class NoSuchParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchParameterException() {
		super();
	}

	public NoSuchParameterException(String string) {
		super(string);
	}
}
