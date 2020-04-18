package scw.beans.property;

import scw.core.Assert;
import scw.core.Constants;

public abstract class AbstractValueFormat implements ValueFormat {
	private String charsetName = Constants.DEFAULT_CHARSET_NAME;

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		Assert.requiredArgument(charsetName != null, "charsetName");
		this.charsetName = charsetName;
	}
}
