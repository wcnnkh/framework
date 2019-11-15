package scw.db;

import scw.sql.Sql;

public final class SqlAsyncExecute implements AsyncExecute {
	private static final long serialVersionUID = 1L;
	private final Sql sql;

	public SqlAsyncExecute(Sql sql) {
		this.sql = sql;
	}

	public void execute(DB db) {
		db.execute(sql);
	}
}
