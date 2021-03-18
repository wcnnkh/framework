package scw.redis;

import scw.data.DataOperations;
import scw.data.cas.CASOperations;

public interface Redis extends DataOperations{
	RedisOperations<String, String> getStringOperations();

	RedisOperations<byte[], byte[]> getBinaryOperations();

	RedisOperations<String, Object> getObjectOperations();

	CASOperations getCASOperations();
}
