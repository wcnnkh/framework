package io.basc.framework.timer.extend;

import java.util.concurrent.locks.Lock;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.db.Database;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskLockFactory;

@ConditionalOnParameters
public class DBTaskLockFactory implements TaskLockFactory {
	private Database db;

	public DBTaskLockFactory(Database db) {
		this.db = db;
		db.createTable(TaskLockTable.class, false);
	}

	public final Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new TableLock(db, taskConfig, executionTime);
	}
}
