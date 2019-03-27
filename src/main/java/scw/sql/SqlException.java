package scw.sql;

import scw.common.exception.NestedRuntimeException;

public class SqlException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public SqlException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SqlException(String msg) {
		super(msg);
	}

	public SqlException(Throwable cause) {
		super(cause);
	}
}
