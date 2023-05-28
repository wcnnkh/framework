package io.basc.framework.beans;

import io.basc.framework.factory.FactoryException;

public class BeansException extends FactoryException {
	private static final long serialVersionUID = 1L;

	public BeansException(String message) {
		super(message);
	}

	public BeansException(Throwable e) {
		super(e);
	}

	public BeansException(String message, Throwable e) {
		super(message, e);
	}
}
