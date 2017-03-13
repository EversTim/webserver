package nl.sogyo.webserver.exceptions;

public class MalformedParameterException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MalformedParameterException() {
		super();
	}

	public MalformedParameterException(String string) {
		super(string);
	}
}
