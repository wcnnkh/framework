package scw.redis;

/**
 * 此类已弃用
 * 
 * @author shuchaowen
 *
 */
public interface Redis extends scw.data.redis.Redis {
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
}
