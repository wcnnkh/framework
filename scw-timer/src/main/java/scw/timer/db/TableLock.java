package scw.timer.db;

import java.util.concurrent.TimeUnit;

import scw.db.DB;
import scw.locks.AbstractLock;
import scw.sql.Sql;

class TableLock extends AbstractLock {
	private DB db;
	private Sql sql;

	public TableLock(DB db, Sql sql) {
		this.db = db;
		this.sql = sql;
	}

	public boolean tryLock() {
		return db.update(sql) != 0;
	}

	public boolean unlock() {
		return true;
	}

	public boolean renewal() {
		return false;
	}

	public boolean renewal(long time, TimeUnit unit) {
		return false;
	}
}
