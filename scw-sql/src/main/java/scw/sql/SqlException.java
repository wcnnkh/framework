package scw.sql;

public class SqlException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SqlException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public SqlException(Sql sql, Throwable cause) {
		this(sql.toString(), cause);
	}

	public SqlException(String msg) {
		super(msg);
	}

	public SqlException(Throwable cause) {
		super(cause);
	}
}
