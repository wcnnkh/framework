package shuchaowen.core.util;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XUtils {
	public static boolean isWin() {
		return System.getProperty("os.name").toLowerCase().startsWith("win");
	}

	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac");
	}

	public static void close(final AutoCloseable... closeables) {
		close(false, closeables);
	}

	public static void close(final boolean throwException, final AutoCloseable... closeables) {
		if (closeables != null) {
			for (AutoCloseable close : closeables) {
				if (close != null) {
					try {
						close.close();
					} catch (Exception e) {
						if (throwException) {
							throw new AutoCloseableException(e);
						} else {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * 获取某闭区间的随机值[min, max]
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandValue(int min, int max) {
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	/**
	 * 求最大公约数
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int greatestCommonDivisor(int a, int b) {
		int gongyue = 0;
		if (a < b) { // 交换a、b的值
			a = a + b;
			b = a - b;
			a = a - b;
		}
		if (a % b == 0) {
			gongyue = b;
		}
		while (a % b > 0) {
			a = a % b;
			if (a < b) {
				a = a + b;
				b = a - b;
				a = a - b;
			}
			if (a % b == 0) {
				gongyue = b;
			}
		}
		return gongyue;
	}

	/**
	 * 获取某数组的随机数
	 * 
	 * @param arr
	 * @return
	 */
	public static int getRandValue(int[] arr) {
		int idx = (int) (Math.random() * arr.length);
		return arr[idx];
	}

	/**
	 * 得到一组数是的最大值或最小值
	 * 
	 * @param isMax
	 * @param num
	 * @return
	 */
	public static long getMaxOrMinNum(boolean isMax, long... num) {
		long temp = num[0];
		for (long n : num) {
			if (n > temp && isMax) {
				temp = n;
			}
		}
		return temp;
	}

	public static String getUUID() {
		String str = UUID.randomUUID().toString();
		char[] cs = new char[32];
		char[] oldCs = str.toCharArray();
		char c;
		int index = 0;
		for (int i = 0; i < oldCs.length; i++) {
			c = oldCs[i];
			switch (c) {
			case '-':
				break;
			default:
				cs[index] = c;
				index++;
				break;
			}
		}
		return new String(cs, 0, 32);
	}

	public static String mergePath(String... path) {
		if (path.length == 0) {
			return null;
		}

		if (path.length == 1) {
			return path[0];
		}

		String p = addPath(path[0], path[1]);
		for (int i = 2; i < path.length; i++) {
			p = addPath(p, path[i]);
		}
		return p;
	}

	private static String addPath(String path1, String path2) {
		String p1 = path1 == null ? "" : path1;
		String p2 = path2 == null ? "" : path2;
		p1 = p1.replaceAll("\\\\", "/");
		p2 = p2.replaceAll("\\\\", "/");

		if (!StringUtils.isNull(p2)) {
			if (!p1.endsWith("/")) {
				p1 = p1 + "/";
			}
		}

		if (!StringUtils.isNull(p1)) {
			if (p2.startsWith("/")) {
				p2 = p2.substring(1);
			}
		}
		return p1 + p2;
	}

	private static final String INNER_IP_PATTERN = "((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))" + "(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|"
			+ "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})";
	/**
	 * 判断是否是内网IP
	 * @param ip
	 * @return
	 */
	public static boolean isInnerIP(String ip) {
		Pattern p = Pattern.compile(INNER_IP_PATTERN);
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}
	
	
}

class AutoCloseableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AutoCloseableException(Throwable e) {
		super(e);
	}
}
