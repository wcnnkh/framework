package io.basc.framework.convert.strings;

import java.math.BigInteger;

import io.basc.framework.util.StringUtils;

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
