package nl.sogyo.webserver;

public class HeaderParameter {
	private String name;
	private String value;

	public HeaderParameter(String line) {
		String[] split = line.split(": ");
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
