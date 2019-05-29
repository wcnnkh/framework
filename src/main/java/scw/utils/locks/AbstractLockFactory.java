package scw.utils.locks;

public abstract class AbstractLockFactory implements LockFactory {
	private final int default_timeout;

	public AbstractLockFactory(int default_timeout) {
		this.default_timeout = default_timeout;
	}

	public Lock getLock(String name) {
		return getLock(name, default_timeout);
	}
	
}
