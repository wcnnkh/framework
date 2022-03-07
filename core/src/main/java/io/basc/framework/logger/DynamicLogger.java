package io.basc.framework.logger;

import io.basc.framework.util.Assert;

public class DynamicLogger implements LoggerWrapper {
	private Logger source;

	public DynamicLogger(Logger source) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
	}

	@Override
	public Logger getSource() {
		return source;
	}

	public void setSource(Logger source) {
		this.source = source;
	}

}
