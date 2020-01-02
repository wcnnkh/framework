package scw.oas.support;

import java.io.Serializable;

import scw.oas.ApiDescription;

public class SimpleApiDescription implements ApiDescription, Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
