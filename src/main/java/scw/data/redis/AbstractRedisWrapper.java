package scw.data.redis;

import scw.data.cas.CASOperations;

public abstract class AbstractRedisWrapper implements Redis {
	public abstract Redis getTargetRedis();

	public RedisOperations<String, String> getStringOperations() {
		return getTargetRedis().getStringOperations();
	}

	public RedisOperations<byte[], byte[]> getBinaryOperations() {
		return getTargetRedis().getBinaryOperations();
	}

	public RedisOperations<String, Object> getObjectOperations() {
		return getTargetRedis().getObjectOperations();
	}

	public <T> RedisOperations<String, T> getSpecifiedTypeOperations(Class<T> type) {
		return getTargetRedis().getSpecifiedTypeOperations(type);
	}

	public CASOperations getCASOperations() {
		return getTargetRedis().getCASOperations();
	}

}
