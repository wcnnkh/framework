package scw.db.locks;

import java.util.concurrent.TimeUnit;

import scw.db.DB;
import scw.locks.RenewableLock;
import scw.locks.RenewableLockFactory;
import scw.util.XUtils;

public class TableLockFactory extends RenewableLockFactory {
	private final DB db;

	public TableLockFactory(DB db) {
		this.db = db;
		db.createTable(LockTable.class);
	}

	@Override
	public RenewableLock getLock(String name, TimeUnit timeUnit, long timeout) {
		return new TableLock(db, name, XUtils.getUUID(), timeUnit, timeout);
	}

}
