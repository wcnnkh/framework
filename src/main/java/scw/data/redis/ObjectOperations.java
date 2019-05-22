package scw.data.redis;

import scw.core.serializer.Serializer;

final class ObjectOperations extends AbstractObjectOperations {
	private final AbstractRedis redis;

	public ObjectOperations(AbstractRedis redis) {
		this.redis = redis;
	}

	@Override
	protected RedisOperations<byte[], byte[]> getBinaryOperations() {
		return redis.getBinaryOperations();
	}

	@Override
	protected RedisOperations<String, String> getStringOperations() {
		return redis.getStringOperations();
	}

	@Override
	protected Serializer getSerializer() {
		return redis.getSerializer();
	}

}
