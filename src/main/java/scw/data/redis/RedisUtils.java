package scw.data.redis;

public final class RedisUtils {
	private static final String OK = "OK";

	public static boolean isOK(String value) {
		return OK.equals(value);
	}
}
