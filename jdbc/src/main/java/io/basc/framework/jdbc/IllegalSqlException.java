package io.basc.framework.jdbc;

public class IllegalSqlException extends SqlException {
	private static final long serialVersionUID = 1L;

	public IllegalSqlException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public IllegalSqlException(String msg) {
		super(msg);
	}

	public IllegalSqlException(Throwable cause) {
		super(cause);
	}
}
