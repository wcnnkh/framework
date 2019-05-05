package scw.data.redis;

import scw.data.redis.serialize.JavaObjectRedisSerialize;
import scw.data.redis.serialize.RedisSerialize;

public final class RedisUtils {
	public static final RedisSerialize DEFAULT_OBJECT_SERIALIZE = new JavaObjectRedisSerialize();

	/**
	 * 设置过期时间为秒
	 */
	public static final String EX = "EX";
	/**
	 * 设置过期时间为毫秒
	 */
	public static final String PX = "PX";
	/**
	 * 只在键不存在时，才对键进行设置操作
	 */
	public static final String NX = "NX";
	/**
	 * 只在键已经存在时，才对键进行设置操作
	 */
	public static final String XX = "XX";

	private static final String OK = "OK";

	public static Boolean isOK(String value) {
		if (value == null) {
			return null;
		}
		return OK.equals(value);
	}
}
