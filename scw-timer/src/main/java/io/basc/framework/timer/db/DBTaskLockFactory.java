package io.basc.framework.timer.db;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.db.DB;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.timer.TaskConfig;
import io.basc.framework.timer.TaskLockFactory;

import java.util.concurrent.locks.Lock;

@Provider
public class DBTaskLockFactory implements TaskLockFactory {
	private DB db;

	public DBTaskLockFactory(DB db) {
		this.db = db;
		db.createTable(TaskLockTable.class, false);
	}

	public final Lock getLock(TaskConfig taskConfig, long executionTime) {
		return new TableLock(db, createLockSql(taskConfig, executionTime));
	}

	protected Sql createLockSql(TaskConfig taskConfig, long executionTime) {
		return new SimpleSql(
				"insert into task_lock_table (taskId, lastTime) value (?, ?) on duplicate key update lastTime=if(lastTime<?, ?, lastTime)",
				taskConfig.getTaskId(), executionTime, executionTime, executionTime);
	}
}
