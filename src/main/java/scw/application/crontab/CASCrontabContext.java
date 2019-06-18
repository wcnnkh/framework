package scw.application.crontab;

import scw.data.cas.CAS;
import scw.data.cas.CASOperations;

public class CASCrontabContext implements CrontabContext {
	private final CASOperations casOperations;
	private final long executionTime;
	private final String name;

	public CASCrontabContext(CASOperations casOperations, String name, long executionTime) {
		this.casOperations = casOperations;
		this.executionTime = executionTime;
		this.name = name;
	}

	public boolean begin() {
		CAS<Long> cas = casOperations.get(name);
		if (cas == null) {
			return casOperations.cas(name, executionTime, 0, 0);
		} else {
			if (executionTime > cas.getValue()) {
				return casOperations.cas(name, executionTime, 0, cas.getCas());
			} else {
				return false;
			}
		}
	}

	public void end() {
	}

	public void error(Throwable e) {
	}

	public void completet() {
	}

}
