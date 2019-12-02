package scw.logger;

public abstract class AbstractLazyLogger extends AbstractLoggerWrapper {
	private final String name;
	private final String placeholder;

	public AbstractLazyLogger(String name, String placeholder) {
		this.placeholder = placeholder;
		this.name = name;
	}

	public final String getPlaceholder() {
		return placeholder;
	}

	public String getName() {
		return name;
	}
}