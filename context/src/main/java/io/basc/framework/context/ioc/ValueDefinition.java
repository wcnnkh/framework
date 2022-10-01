package io.basc.framework.context.ioc;

public class ValueDefinition extends AutowiredDefinition{
	private boolean listener;
	private String charsetName;
	private Class<? extends ValueProcessor> valueProcessor;

	public boolean isListener() {
		return listener;
	}

	public void setListener(boolean listener) {
		this.listener = listener;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	public Class<? extends ValueProcessor> getValueProcessor() {
		return valueProcessor;
	}

	public void setValueProcessor(Class<? extends ValueProcessor> valueProcessor) {
		this.valueProcessor = valueProcessor;
	}
}
