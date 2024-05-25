package io.basc.framework.orm;

import io.basc.framework.mapper.entity.MappingException;

public class OrmException extends MappingException {
	private static final long serialVersionUID = 1L;

	public OrmException(String msg) {
		super(msg);
	}

	public OrmException(Throwable cause) {
		super(cause);
	}

	public OrmException(String message, Throwable cause) {
		super(message, cause);
	}
}
