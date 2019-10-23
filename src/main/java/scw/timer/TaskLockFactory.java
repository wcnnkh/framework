package scw.timer;

import scw.beans.annotation.AutoImpl;
import scw.locks.Lock;
import scw.timer.db.DBTaskLockFactory;
import scw.timer.support.CASTaskLockFactory;
import scw.timer.support.EmptyTaskLockFactory;

@AutoImpl({ CASTaskLockFactory.class, DBTaskLockFactory.class, EmptyTaskLockFactory.class })
public interface TaskLockFactory {
	Lock getLock(String taskId, long executionTime);
}
