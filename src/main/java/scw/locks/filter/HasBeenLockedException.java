package scw.locks.filter;

/**
 * 已经被锁了
 * @author shuchaowen
 *
 */
public class HasBeenLockedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String lockKey;
	
	protected HasBeenLockedException(){
	}

	public HasBeenLockedException(String lockKey) {
		this.lockKey = lockKey;
	}

	public String getLockKey() {
		return lockKey;
	}
}
