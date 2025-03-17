package run.soeasy.framework.jdbc;

import run.soeasy.framework.lang.NestedRuntimeException;

public class SqlException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public SqlException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SqlException(Sql sql, Throwable cause) {
		this("Execute - " + sql.toString(), cause);
	}

	public SqlException(String msg) {
		super(msg);
	}

	public SqlException(Throwable cause) {
		super(cause);
	}
}
