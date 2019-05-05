package scw.data.redis.serialize;

/**
 * redis序列化
 * @author asus1
 *
 * @param <K>
 * @param <V>
 */
public interface RedisSerialize {

	byte[] serialize(String key, Object data);

	<T> T deserialize(String key, byte[] data);
}
