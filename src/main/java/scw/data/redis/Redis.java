package scw.data.redis;

public interface Redis {

	RedisOperations<String, String> getStringOperations();

	RedisOperations<byte[], byte[]> getBinaryOperations();

	RedisOperations<String, Object> getObjectOperations();

	<T> RedisOperations<String, T> getSpecifiedTypeOperations(Class<T> type);
}
