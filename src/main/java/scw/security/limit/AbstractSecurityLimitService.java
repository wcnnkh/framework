package scw.security.limit;

public abstract class AbstractSecurityLimitService implements SecurityLimitService{
	private final int maxLimit;
	private final int timeout;
	
	public AbstractSecurityLimitService(int maxLimit, int timeout){
		this.maxLimit = maxLimit;
		this.timeout = timeout;
	}

	public final int getMaxLimit() {
		return maxLimit;
	}

	public final int getTimeout() {
		return timeout;
	}
}
