package scw.data.redis;

import scw.data.cas.CASOperations;
import scw.io.serializer.Serializer;

public abstract class AbstractRedis implements Redis {
	private final AbstractObjectOperations objectOperations = new ObjectOperations(this, getCharsetName());
	private final CASOperations casOperations = new RedisCASOperations(objectOperations);

	protected abstract Serializer getSerializer();

	protected abstract String getCharsetName();

	public RedisOperations<String, Object> getObjectOperations() {
		return objectOperations;
	}

	public CASOperations getCASOperations() {
		return casOperations;
	}

	public <T> RedisOperations<String, T> getSpecifiedTypeOperations(Class<T> type) {
		return new SpecifiedTypeOperations<T>(type, getBinaryOperations(), getStringOperations(), getSerializer());
	}
}
