package io.basc.framework.timer.extend;

import java.util.concurrent.TimeUnit;

import io.basc.framework.db.DB;
import io.basc.framework.locks.RenewableLock;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.timer.TaskConfig;

class TableLock extends RenewableLock {
	private DB db;
	private TaskConfig taskConfig;
	private long executionTime;

	public TableLock(DB db, TaskConfig taskConfig, long executionTime) {
		super(TimeUnit.MINUTES, 5);
		this.db = db;
		this.taskConfig = taskConfig;
		this.executionTime = executionTime;
	}

	public boolean tryLock() {
		TaskLockTable taskLockTable = new TaskLockTable();
		taskLockTable.setTaskId(taskConfig.getTaskId());
		taskLockTable.setLastTime(executionTime);
		if(db.saveIfAbsent(taskLockTable)) {
			return true;
		}
		Sql updateSql = new SimpleSql("update task_lock_table set lastTime=? where taskId=? and lastTime<?", executionTime, taskConfig.getTaskId(), executionTime);
		return db.update(updateSql) > 0;
	}

	public void unlock() {
	}

	public boolean renewal(long time, TimeUnit unit) {
		return false;
	}
}
