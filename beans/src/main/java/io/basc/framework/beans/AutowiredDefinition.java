package io.basc.framework.beans;

public class AutowiredDefinition {
	private String[] names;
	private boolean required;

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
