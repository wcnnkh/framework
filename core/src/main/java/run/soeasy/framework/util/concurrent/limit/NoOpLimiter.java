package run.soeasy.framework.util.concurrent.limit;

import java.util.concurrent.locks.Lock;

import run.soeasy.framework.util.concurrent.locks.NoOpLock;

/**
 * 无限制
 * 
 * @author shuchaowen
 *
 */
public class NoOpLimiter extends DisposableLimiter {

	@Override
	public Lock getResource() {
		return isLimited() ? NoOpLock.DEAD : NoOpLock.NO;
	}

}
