package scw.json;

import java.math.BigDecimal;

public final class TypeUtils {
	public static byte byteValue(BigDecimal decimal) {
		if (decimal == null) {
			return 0;
		}

		int scale = decimal.scale();
		if (scale >= -100 && scale <= 100) {
			return decimal.byteValue();
		}

		return decimal.byteValueExact();
	}

	public static Byte castToByte(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof BigDecimal) {
			return byteValue((BigDecimal) value);
		}

		if (value instanceof Number) {
			return ((Number) value).byteValue();
		}

		if (value instanceof String) {
			String strVal = (String) value;
			if (strVal.length() == 0 //
					|| "null".equals(strVal) //
					|| "NULL".equals(strVal)) {
				return null;
			}
			return Byte.parseByte(strVal);
		}
		throw new JSONException("can not cast to byte, value : " + value);
	}
}
