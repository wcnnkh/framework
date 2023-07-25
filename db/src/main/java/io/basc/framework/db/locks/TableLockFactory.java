package io.basc.framework.db.locks;

import io.basc.framework.db.Database;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.locks.RenewableLockFactory;
import io.basc.framework.util.XUtils;

import java.util.concurrent.TimeUnit;

public class TableLockFactory extends RenewableLockFactory {
	private final Database db;

	public TableLockFactory(Database db) {
		this.db = db;
		db.createTable(LockTable.class);
	}

	@Override
	public RenewableLock getLock(String name, TimeUnit timeUnit, long timeout) {
		return new TableLock(db, name, XUtils.getUUID(), timeUnit, timeout);
	}

}
