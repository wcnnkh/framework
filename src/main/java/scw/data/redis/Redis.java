package scw.data.redis;

public interface Redis {

	RedisOperations<String, String> getStringOperations();

	RedisOperations<byte[], byte[]> getBinaryOperations();
}
