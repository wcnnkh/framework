package io.basc.framework.timer.extend;

import java.util.concurrent.locks.Lock;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.db.DB;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskLockFactory;

@Provider
public class DBTaskLockFactory implements TaskLockFactory {
	private DB db;

	public DBTaskLockFactory(DB db) {
		this.db = db;
		db.createTable(TaskLockTable.class, false);
	}

	public final Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new TableLock(db, taskConfig, executionTime);
	}
}
