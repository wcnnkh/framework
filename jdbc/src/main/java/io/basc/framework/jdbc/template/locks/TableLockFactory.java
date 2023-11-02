package io.basc.framework.jdbc.template.locks;

import java.util.concurrent.TimeUnit;

import io.basc.framework.jdbc.template.Database;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.locks.RenewableLockFactory;
import io.basc.framework.util.XUtils;

public class TableLockFactory extends RenewableLockFactory {
	private final Database database;

	public TableLockFactory(Database database) {
		this.database = database;
		database.createTable(LockTable.class);
	}

	@Override
	public RenewableLock getLock(String name, TimeUnit timeUnit, long timeout) {
		return new TableLock(database, name, XUtils.getUUID(), timeUnit, timeout);
	}

}
