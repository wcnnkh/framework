package scw.util.value.property;

import java.io.Serializable;

import scw.util.value.Value;

public class Property implements Serializable {
	private static final long serialVersionUID = 1L;
	private String key;
	private Value value;

	public Property(String key, Value value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Value getValue() {
		return value;
	}
}
