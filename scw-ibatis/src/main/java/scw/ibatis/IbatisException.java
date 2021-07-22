package scw.ibatis;

import scw.lang.NestedRuntimeException;

public class IbatisException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public IbatisException(String msg) {
		super(msg);
	}

	public IbatisException(Throwable cause) {
		super(cause);
	}

	public IbatisException(String message, Throwable cause) {
		super(message, cause);
	}
}
