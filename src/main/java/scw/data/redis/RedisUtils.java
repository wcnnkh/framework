package scw.data.redis;

public final class RedisUtils {
	public static String notNullScript(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" ~= nil");
		sb.append(" or (type(").append(name).append(") == 'boolean' and ").append(name).append(")");
		return sb.toString();
	}

	public static String isNullScript(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" == nil");
		sb.append(" or (type(").append(name).append(") == 'boolean' and ").append(name).append(" == false)");
		return sb.toString();
	}
}
