package nl.sogyo.webserver;

import nl.sogyo.webserver.exceptions.MalformedParameterException;

public class Parameter {
	private String name;
	private String value;

	public Parameter(String line) {
		String[] split = line.split("=");
		if (split.length != 2) {
			throw new MalformedParameterException("Malformed parameter detected: " + line + ".");
		}
		this.name = split[0].trim();
		this.value = split[1].trim();
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}
}
