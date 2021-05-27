package scw.web.pattern;

import scw.util.Supplier;

public abstract class HttpPatternRegsitration<T> implements Supplier<T> {
	private final HttpPattern pattern;
	private final T service;

	public HttpPatternRegsitration(HttpPattern pattern, T service) {
		this.pattern = pattern;
		this.service = service;
	}

	public HttpPattern getPattern() {
		return pattern;
	}

	public T get() {
		return service;
	}

	public abstract boolean unregister();
}
