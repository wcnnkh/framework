package scw.data.redis;

import scw.data.cas.CASOperations;

public interface Redis {

	RedisOperations<String, String> getStringOperations();

	RedisOperations<byte[], byte[]> getBinaryOperations();

	RedisOperations<String, Object> getObjectOperations();

	<T> RedisOperations<String, T> getSpecifiedTypeOperations(Class<T> type);
	
	CASOperations getCASOperations();
}
