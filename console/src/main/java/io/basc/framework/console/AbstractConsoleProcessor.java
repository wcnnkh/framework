package io.basc.framework.console;

import io.basc.framework.util.Assert;

public abstract class AbstractConsoleProcessor<T> implements ConsoleProcessor<T> {
	private String pattern;
	private final String name;

	public AbstractConsoleProcessor(String name) {
		Assert.requiredArgument(name != null, name);
		this.name = name;
	}

	@Override
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String getName() {
		return name;
	}
}
