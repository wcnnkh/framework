package io.basc.framework.jdbc.template.locks;

import java.util.concurrent.TimeUnit;

import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.template.Database;
import io.basc.framework.locks.RenewableLock;

class TableLock extends RenewableLock {
	public static final String TABLE_NAME = "lock_table";

	private final Database database;
	private final String name;
	private final String value;

	public TableLock(Database database, String name, String value, TimeUnit timeUnit, long timeout) {
		super(timeUnit, timeout);
		this.database = database;
		this.name = name;
		this.value = value;
	}

	private boolean tryLock(long cts) {
		LockTable lockTable = new LockTable();
		lockTable.setCreateTime(cts);
		lockTable.setExpirationTime(lockTable.getCreateTime() + getTimeout(TimeUnit.MILLISECONDS));
		lockTable.setName(name);
		lockTable.setValue(value);
		if (database.saveIfAbsent(lockTable)) {
			return true;
		}

		if (cts > lockTable.getExpirationTime()) {
			// 到期了
			Sql sql = new SimpleSql("update " + TABLE_NAME + " set value=? where name=? and expirationTime < ?", value,
					name, cts);
			return database.update(sql) > 0;
		}
		return false;
	}

	public boolean tryLock() {
		if (tryLock(System.currentTimeMillis())) {
			autoRenewal();
			return true;
		}
		return false;
	}

	public void unlock() {
		cancelAutoRenewal();
		Sql sql = new SimpleSql("delete from " + TABLE_NAME + " where name=? and value=?", name, value);
		database.update(sql);
	}

	@Override
	public boolean renewal(long time, TimeUnit unit) {
		Sql sql = new SimpleSql(
				"update " + TABLE_NAME + " set expirationTime=expirationTime+? where name=? and value = ?",
				unit.toMillis(time), name, value);
		return database.update(sql) > 0;
	}

}
