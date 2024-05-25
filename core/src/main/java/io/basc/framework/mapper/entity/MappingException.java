package io.basc.framework.mapper.entity;

import io.basc.framework.convert.ConversionException;

public class MappingException extends ConversionException {
	private static final long serialVersionUID = 1L;

	public MappingException(String message) {
		super(message);
	}

	public MappingException(Throwable cause) {
		super(cause);
	}

	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}

}
