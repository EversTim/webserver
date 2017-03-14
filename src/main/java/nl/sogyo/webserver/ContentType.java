package nl.sogyo.webserver;

public enum ContentType {
	NONE("none"), TEXT_HTML("text/html"), TEXT_CSS("text/css"), IMAGE_JPEG("image/jpeg");

	private String type;

	private ContentType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public String getGeneralType() {
		return this.type.split("/")[0];
	}

	public String getSpecificType() {
		return this.type.split("/")[1];
	}
}
