package scw.db.async;

import scw.async.AbstractAsyncRunnable;
import scw.db.DB;

public abstract class AsyncRunnableDB extends AbstractAsyncRunnable {
	private static final long serialVersionUID = 1L;
	private transient DB db;

	public DB getDb() {
		return db;
	}

	public void setDb(DB db) {
		this.db = db;
	}
}
