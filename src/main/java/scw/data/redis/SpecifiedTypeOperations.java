package scw.data.redis;

import scw.serializer.Serializer;

final class SpecifiedTypeOperations<T> extends AbstractSpecifiedTypeOperations<T> {
	private final RedisOperations<byte[], byte[]> binaryOperations;
	private final RedisOperations<String, String> stringOperations;
	private final Serializer serializer;

	public SpecifiedTypeOperations(Class<T> type, RedisOperations<byte[], byte[]> binaryOperations,
			RedisOperations<String, String> stringOperations, Serializer serializer) {
		super(type);
		this.binaryOperations = binaryOperations;
		this.stringOperations = stringOperations;
		this.serializer = serializer;
	}

	@Override
	protected RedisOperations<byte[], byte[]> getBinaryOperations() {
		return binaryOperations;
	}

	@Override
	protected RedisOperations<String, String> getStringOperations() {
		return stringOperations;
	}

	@Override
	protected Serializer getSerializer() {
		return serializer;
	}
}
