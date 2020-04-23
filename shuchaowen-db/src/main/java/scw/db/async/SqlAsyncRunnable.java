package scw.db.async;

import scw.sql.Sql;

public final class SqlAsyncRunnable extends AsyncRunnableDB {
	private static final long serialVersionUID = 1L;
	private final Sql sql;

	public SqlAsyncRunnable(Sql sql) {
		this.sql = sql;
	}

	public Object call() throws Exception {
		getDb().execute(sql);
		return null;
	}
}
