package scw.db.async;

import scw.async.AbstractAsyncRunnable;
import scw.db.DB;

public abstract class AsyncExecute extends AbstractAsyncRunnable {
	private static final long serialVersionUID = 1L;
	private transient DB db;

	public DB getDb() {
		return db;
	}

	protected void setDb(DB db) {
		this.db = db;
	}
}
