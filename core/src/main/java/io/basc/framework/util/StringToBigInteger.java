package io.basc.framework.util;

import java.math.BigInteger;

public class StringToBigInteger extends StringToNumber {

	public StringToBigInteger(boolean unsigned, int radix) {
		super(unsigned, radix);
	}

	@Override
	public BigInteger apply(String source) {
		String value = format(source);
		return StringUtils.isEmpty(value) ? null : new BigInteger(value, getRadix());
	}
}
