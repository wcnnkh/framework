package scw.util;

import java.io.Serializable;

public class StringValue extends AbstractValue implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;

	public StringValue(String value) {
		this.value = value;
	}

	public String getAsString() {
		return value;
	}
}
