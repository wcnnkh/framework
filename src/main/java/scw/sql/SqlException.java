package scw.sql;

import scw.core.NestedRuntimeException;

public class SqlException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public SqlException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SqlException(Throwable cause) {
		super(cause);
	}
}
