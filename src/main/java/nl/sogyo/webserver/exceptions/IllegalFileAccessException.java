package nl.sogyo.webserver.exceptions;

public class IllegalFileAccessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public IllegalFileAccessException() {
		super();
	}

	public IllegalFileAccessException(String str) {
		super(str);
	}
}
