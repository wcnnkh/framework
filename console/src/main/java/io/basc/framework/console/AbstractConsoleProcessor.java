package io.basc.framework.console;

public abstract class AbstractConsoleProcessor<T> implements ConsoleProcessor<T> {
	private String pattern;
	private String name;

	public AbstractConsoleProcessor(String name) {
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

	public void setName(String name) {
		this.name = name;
	}
}
