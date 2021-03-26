package scw.timer.db;

import java.util.concurrent.TimeUnit;

import scw.db.DB;
import scw.locks.RenewableLock;
import scw.sql.Sql;

class TableLock extends RenewableLock {
	private DB db;
	private Sql sql;

	public TableLock(DB db, Sql sql) {
		super(TimeUnit.MINUTES, 5);
		this.db = db;
		this.sql = sql;
	}

	public boolean tryLock() {
		return db.update(sql) != 0;
	}

	public void unlock() {
	}

	public boolean renewal(long time, TimeUnit unit) {
		return false;
	}
}
