package scw.application.crontab;

import scw.data.cas.CAS;
import scw.data.cas.CASOperations;

//TODO 还未完成
public class CASCrontabContext implements CrontabContext {
	private final CASOperations casOperations;
	private long v;
	private long cas;
	private final String name;

	public CASCrontabContext(CASOperations casOperations, String name) {
		this.casOperations = casOperations;
		CAS<Long> cas = casOperations.get(name);
		if (cas == null) {
			v = 0;
			this.cas = 0;
		} else {
			v = cas.getValue();
			this.cas = cas.getCas();
		}
		this.name = name;
	}

	public boolean begin() {
		boolean b = casOperations.cas(name, v + 1, 0, cas);
		return b;
	}

	public void end() {
		// TODO Auto-generated method stub

	}

	public void error(Throwable e) {
		// TODO Auto-generated method stub

	}

	public void completet() {
		// TODO Auto-generated method stub

	}

}
