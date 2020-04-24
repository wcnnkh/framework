package scw.data.redis;

import java.io.IOException;

import scw.io.Serializer;
import scw.lang.NestedRuntimeException;
import scw.lang.StringCodec;

public final class ObjectOperations extends AbstractRedisOperationsWrapper<String, byte[], Object, byte[]> {
	private final RedisOperations<byte[], byte[]> redisOperations;
	private final StringCodec stringCodec;
	private final Serializer serializer;

	public ObjectOperations(RedisOperations<byte[], byte[]> redisOperations, Serializer serializer,
			StringCodec stringCodec) {
		this.redisOperations = redisOperations;
		this.stringCodec = stringCodec;
		this.serializer = serializer;
	}

	@Override
	protected RedisOperations<byte[], byte[]> getRedisOperations() {
		return redisOperations;
	}

	@Override
	protected byte[] encodeKey(String key) {
		if(key == null){
			return null;
		}
		
		return stringCodec.encode(key);
	}

	@Override
	protected String decodeKey(byte[] key) {
		if(key == null){
			return null;
		}
		
		return stringCodec.decode(key);
	}

	@Override
	protected byte[] encodeValue(Object value) {
		if(value == null){
			return null;
		}
		
		try {
			return serializer.serialize(value);
		} catch (IOException e) {
			throw new NestedRuntimeException(e);
		}
	}

	@Override
	protected Object decodeValue(byte[] value) {
		if(value == null){
			return null;
		}
		
		try {
			return serializer.deserialize(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
