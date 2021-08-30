package io.basc.framework.util;

import java.util.Random;

public final class RandomUtils {
	private RandomUtils() {
	};

	public final static char[] CAPITAL_LETTERS = { 'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
			'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', };

	public final static char[] LOWERCASE_LETTERS = { 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	public final static char[] NUMBERIC_CHARACTER = { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9' };

	/**
	 * 放在一起容易分辨的字符
	 */
	public final static char[] EASY_TO_DISTINGUISH = { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'a', 'c', 'd', 'e', 'f', 'h', 'k', 'm',
			'n', 'p', 'r', 's', 't', 'v', 'w', 'y', 'A', 'B', 'C', 'E', 'F',
			'G', 'H', 'K', 'M', 'N', 'R', 'S', 'T', 'V', 'W', 'Y' };

	public final static char[] ALL = StringUtils.mergeCharArray(
			NUMBERIC_CHARACTER, LOWERCASE_LETTERS, CAPITAL_LETTERS);

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
	 * 获取某闭区间的随机值[min, max]
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandValue(int min, int max) {
		return (int) (Math.random() * (max - min + 1)) + min;
	}

	public static String getRandomStr(int length) {
		return new String(getRandomCharArray(ALL, length));
	}

	public static String getRandomStr(String randomStr, int length) {
		char[] cArr = new char[length];
		for (int i = 0, size = randomStr.length(); i < length; ++i) {
			cArr[i] = randomStr.charAt(new Random().nextInt(size));
		}
		return new String(cArr);
	}

	public static char[] getRandomCharArray(char[] randomCharArray, int length) {
		char[] cArr = new char[length];
		for (int i = 0; i < length; ++i) {
			cArr[i] = randomCharArray[new Random()
					.nextInt(randomCharArray.length)];
		}
		return cArr;
	}

	/**
	 * 获取指定长度的随机数字组成的字符串
	 * 
	 * @param len
	 * @return
	 */
	public static String getNumCode(int len) {
		return new String(getRandomCharArray(RandomUtils.NUMBERIC_CHARACTER,
				len));
	}
}
