package shuchaowen.common;

/**
 * 10进制整数与任意进制之间的转换
 * 
 * @author shuchaowen
 *
 */
public final class LongNumberConversion {
	private final char[] digits;

	public LongNumberConversion(char[] digits) {
		this.digits = digits;
	}

	public String encode(long number) {
		return encode(number, 65);
	}

	public String encode(long number, int maxLen) {
		char[] buf = new char[maxLen];
		int charPos = buf.length;
		do {
			buf[--charPos] = digits[(int) (number % digits.length)];
			number = number / digits.length;
		} while (number != 0);
		return new String(buf, charPos, (maxLen - charPos));
	}

	/**
	 * 可能会溢出
	 * 
	 * @param number
	 * @return
	 */
	public long decode(String number) {
		long result = 0;
		for (int i = number.length() - 1; i >= 0; i--) {
			for (int j = 0; j < digits.length; j++) {
				if (number.charAt(i) == digits[j]) {
					result += j * (long) Math.pow(digits.length, (number.length() - 1 - i));
				}
			}
		}
		return result;
	}
}
