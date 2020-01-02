package scw.beans.property;

public abstract class AbstractCharsetNameValueFormat implements ValueFormat {
	private final String charsetName;

	public AbstractCharsetNameValueFormat(String charsetName) {
		this.charsetName = charsetName;
	}

	public final String getCharsetName() {
		return charsetName;
	}
}
