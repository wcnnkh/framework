package io.basc.framework.context.ioc;

import io.basc.framework.beans.BeansException;

public class ValueException extends BeansException {
	private static final long serialVersionUID = 1L;

	public ValueException(String message, Throwable e) {
		super(message, e);
	}
}
