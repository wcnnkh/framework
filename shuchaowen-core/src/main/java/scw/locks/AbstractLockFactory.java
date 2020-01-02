package scw.locks;

import java.util.concurrent.TimeUnit;

import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public abstract class AbstractLockFactory implements LockFactory {
	private static final long DEFAULT_TIMEOUT = StringUtils
			.parseLong(SystemPropertyUtils.getProperty("locks.default.timeout"), 60);

	public Lock getLock(String name) {
		return getLock(name, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
	}

}
