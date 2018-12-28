package scw.locks;

import scw.common.exception.ShuChaoWenRuntimeException;

public abstract class AbstractLock implements Lock {
	public void lockWait(long millis, int nanos) throws InterruptedException {
		while (!lock()) {
			Thread.sleep(millis, nanos);
		}
	}

	public void lockWait() {
		try {
			lockWait(2, 0);
		} catch (InterruptedException e) {
			throw new ShuChaoWenRuntimeException();
		} // 5毫秒试一次
	}
}
