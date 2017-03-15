package nl.sogyo.webserver.exceptions;

public class MalformedRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public MalformedRequestException() {
		super();
	}

	public MalformedRequestException(String string) {
		super(string);
	}
}
