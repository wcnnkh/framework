package scw.timer;

import scw.beans.annotation.AutoImpl;
import scw.locks.Lock;
import scw.timer.lock.CASTaskLockFactory;
import scw.timer.lock.DBTaskLockFactory;
import scw.timer.lock.EmptyTaskLockFactory;

@AutoImpl({ CASTaskLockFactory.class, DBTaskLockFactory.class, EmptyTaskLockFactory.class })
public interface TaskLockFactory {
	Lock getLock(String taskId, long executionTime);
}
