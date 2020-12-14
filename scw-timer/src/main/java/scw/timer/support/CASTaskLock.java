package scw.timer.support;

import java.util.concurrent.TimeUnit;

import scw.data.cas.CAS;
import scw.data.cas.CASOperations;
import scw.locks.AbstractLock;

public class CASTaskLock extends AbstractLock {
	private final CASOperations casOperations;
	private final long executionTime;
	private final String taskId;

	public CASTaskLock(CASOperations casOperations, String taskId, long executionTime) {
		this.casOperations = casOperations;
		this.executionTime = executionTime;
		this.taskId = taskId;
	}

	public boolean tryLock() {
		CAS<Long> cas = casOperations.get(taskId);
		if (cas == null) {
			return casOperations.cas(taskId, executionTime, 0, 0);
		} else {
			if (executionTime > cas.getValue()) {
				return casOperations.cas(taskId, executionTime, 0, cas.getCas());
			} else {
				return false;
			}
		}
	}

	public boolean unlock() {
		return true;
	}

	public boolean renewal() {
		return false;
	}

	public boolean renewal(long time, TimeUnit unit) {
		return false;
	}

}
