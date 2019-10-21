package scw.timer.lock;

import scw.db.DB;
import scw.locks.Lock;
import scw.timer.TaskLockFactory;

public final class DBTaskLockFactory implements TaskLockFactory {
	private DB db;

	public DBTaskLockFactory(DB db) {
		this.db = db;
		db.createTable(TaskLockTable.class);
	}

	public Lock getLock(String taskId, long executionTime) {
		return new TableLock(db, taskId, executionTime);
	}

}
