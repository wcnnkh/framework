package io.basc.framework.timer.support;

import io.basc.framework.data.cas.CAS;
import io.basc.framework.data.cas.CasKeyValueOperations;
import io.basc.framework.locks.AbstractLock;

public class CASTaskLock extends AbstractLock {
	private final CasKeyValueOperations casOperations;
	private final long executionTime;
	private final String taskId;

	public CASTaskLock(CasKeyValueOperations casOperations, String taskId, long executionTime) {
		this.casOperations = casOperations;
		this.executionTime = executionTime;
		this.taskId = taskId;
	}

	public boolean tryLock() {
		CAS<Long> cas = casOperations.gets(Long.class, taskId);
		if (cas == null) {
			return casOperations.cas(taskId, executionTime, long.class, 0);
		} else {
			if (executionTime > cas.getValue()) {
				return casOperations.cas(taskId, executionTime, long.class, cas.getCas());
			} else {
				return false;
			}
		}
	}

	public void unlock() {
	}
}
