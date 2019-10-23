package scw.timer.db;

import scw.db.DB;
import scw.locks.AbstractLock;
import scw.sql.SimpleSql;

class TableLock extends AbstractLock {
	private static final String SQL = "update task_lock_table set lastTime=? where taskId=? and lastTime<?";
	private DB db;
	private String taskId;
	private long lastTime;

	public TableLock(DB db, String taskId, long lastTime) {
		this.db = db;
		this.taskId = taskId;
		this.lastTime = lastTime;
	}

	public boolean tryLock() {
		return db.update(new SimpleSql(SQL, lastTime, taskId, lastTime)) != 0;
	}

	public void unlock() {
		// ignore
	}
}
