package scw.data.cache;

public abstract class AbstractMemoryCache implements MemoryCache {
	private volatile long lastTouch;
	private volatile int exp;

	public AbstractMemoryCache() {
		this.lastTouch = System.currentTimeMillis();
	}

	public void setExpire(int exp) {
		this.exp = exp;
	}

	public void touch() {
		this.lastTouch = System.currentTimeMillis();
	}

	public boolean isExpire(long currentTimeMillis) {
		return exp <= 0 ? false : (currentTimeMillis - lastTouch) > exp;
	}

	public boolean setIfAbsent(Object value) {
		if (!isExpire(System.currentTimeMillis())) {
			return false;
		}

		set(value);
		return true;
	}
}
