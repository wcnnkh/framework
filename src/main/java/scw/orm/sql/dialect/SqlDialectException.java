package scw.orm.sql.dialect;

import scw.orm.ORMException;

public class SqlDialectException extends ORMException {
	private static final long serialVersionUID = 1L;

	public SqlDialectException(String message, Throwable e) {
		super(message, e);
	}
}
