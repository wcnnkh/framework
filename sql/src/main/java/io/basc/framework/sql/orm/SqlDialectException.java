package io.basc.framework.sql.orm;

import io.basc.framework.sql.SqlException;

/**
 * 转换为sql方言进发生的错误
 * 
 * @author 35984
 *
 */
public class SqlDialectException extends SqlException {
	private static final long serialVersionUID = 1L;

	public SqlDialectException(String message) {
		super(message);
	}

	public SqlDialectException(String message, Throwable e) {
		super(message, e);
	}
}
