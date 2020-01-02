package scw.oas.support;

import java.util.List;

import scw.oas.ApiParameter;

public class SimpleApiParameter extends SimpleApiDescription implements ApiParameter {
	private static final long serialVersionUID = 1L;
	private boolean required;
	private String type;
	public Object defaultValue;
	private int maxLength;
	private List<? extends ApiParameter> subList;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<? extends ApiParameter> getSubList() {
		return subList;
	}

	public void setSubList(List<? extends ApiParameter> subList) {
		this.subList = subList;
	}
}
