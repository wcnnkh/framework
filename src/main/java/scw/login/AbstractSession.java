package scw.login;

public abstract class AbstractSession implements Session {
	private final long creationTime;
	private final String id;
	private int maxInactiveInterval;
	private long lastAccessedTime;

	public AbstractSession(long creationTime, String id,
			int maxInactiveInterval, long lastAccessedTime) {
		this.creationTime = creationTime;
		this.id = id;
		this.maxInactiveInterval = maxInactiveInterval;
		this.lastAccessedTime = lastAccessedTime;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getId() {
		return id;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}
}
