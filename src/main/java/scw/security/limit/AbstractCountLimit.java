package scw.security.limit;

public abstract class AbstractCountLimit implements CountLimit {
	private final CountLimitConfig countLimitConfig;

	public AbstractCountLimit(CountLimitConfig countLimitConfig) {
		this.countLimitConfig = countLimitConfig;
	}

	public CountLimitConfig getCountLimitConfig() {
		return countLimitConfig;
	}
}
