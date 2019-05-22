package scw.data.redis;

import scw.core.serializer.Serializer;

public abstract class AbstractRedis implements Redis {
	private final RedisOperations<String, Object> objectOperations = new ObjectOperations(getBinaryOperations(),
			getStringOperations(), getSerializer());

	protected abstract Serializer getSerializer();

	public RedisOperations<String, Object> getObjectOperations() {
		return objectOperations;
	}

	public <T> RedisOperations<String, T> getSpecifiedTypeOperations(Class<T> type) {
		return new SpecifiedTypeOperations<T>(type, getBinaryOperations(), getStringOperations(), getSerializer());
	}
}
