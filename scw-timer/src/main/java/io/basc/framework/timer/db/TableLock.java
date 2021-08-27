package io.basc.framework.timer.db;

import io.basc.framework.db.DB;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.sql.Sql;

import java.util.concurrent.TimeUnit;

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
