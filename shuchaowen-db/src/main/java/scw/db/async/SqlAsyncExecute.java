package scw.db.async;

import scw.sql.Sql;

public final class SqlAsyncExecute extends AsyncExecute {
	private static final long serialVersionUID = 1L;
	private final Sql sql;

	public SqlAsyncExecute(Sql sql) {
		this.sql = sql;
	}

	public Object call() throws Exception {
		getDb().execute(sql);
		return null;
	}
}
