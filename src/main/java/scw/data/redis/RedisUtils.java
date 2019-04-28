package scw.data.redis;

public final class RedisUtils {
	private static final String OK = "OK";

	public static Boolean isOK(String value) {
		if (value == null) {
			return null;
		}
		return OK.equals(value);
	}
}
