package io.basc.framework.util;

import java.math.BigDecimal;

public class StringToBigDecimal extends StringToNumber {

	public StringToBigDecimal(boolean unsigned, int radix) {
		super(unsigned, radix);
	}

	@Override
	public BigDecimal apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return new BigDecimal(value);
	}
}
