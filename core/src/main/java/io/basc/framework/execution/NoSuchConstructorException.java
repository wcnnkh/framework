package io.basc.framework.execution;

import java.util.NoSuchElementException;

import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.element.Elements;

public class NoSuchConstructorException extends NoSuchElementException {
	private static final long serialVersionUID = 1L;

	public NoSuchConstructorException(String message) {
		super(message);
	}

	public NoSuchConstructorException(Elements<Class<?>> parameterTypes) {
		this("Unable to match " + parameterTypes + " to constructor");
	}

	public NoSuchConstructorException(Parameters parameters) {
		this("Unable to match " + parameters + " to constructor");
	}
}
