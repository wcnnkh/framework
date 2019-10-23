package scw.timer.db;

import scw.db.DB;
import scw.locks.Lock;
import scw.timer.TaskConfig;
import scw.timer.TaskLockFactory;

public final class DBTaskLockFactory implements TaskLockFactory {
	private DB db;

	public DBTaskLockFactory(DB db) {
		this.db = db;
		db.createTable(TaskLockTable.class);
	}

	public Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new TableLock(db, taskConfig.getTaskId(), executionTime);
	}

}
