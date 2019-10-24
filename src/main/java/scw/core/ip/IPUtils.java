package scw.core.ip;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;

public final class IPUtils {
	/**
	 * 本地ip
	 */
	private static final String[] LOCAL_IP = new String[] { "127.0.0.1", "::1" };

	public static boolean contains(String ip, String... contains) {
		if (StringUtils.isEmpty(ip) || ArrayUtils.isEmpty(contains)) {
			return false;
		}

		String[] arr = StringUtils.commonSplit(ip);
		if (ArrayUtils.isEmpty(arr)) {
			return false;
		}

		for (String v : arr) {
			for (String c : contains) {
				if (v.equals(c)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isLocalIP(String ip) {
		return contains(ip, LOCAL_IP);
	}

}
