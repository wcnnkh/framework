package io.basc.framework.util;

public class Throwables extends Throwable implements Document<Throwable> {
	private static final long serialVersionUID = 1L;
	private final Elements<Throwable> elements;

	public Throwables(Elements<Throwable> elements) {
		super("Multiple Throwables");
		this.elements = elements;
	}

	@Override
	public Elements<Throwable> getElements() {
		return elements;
	}
}
