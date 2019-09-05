package scw.core.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import scw.core.StringEmptyVerification;
import scw.core.ValueFactory;
import scw.core.Verification;
import scw.json.JSONUtils;

public class StringParse implements Verification<CharSequence>, ValueFactory<String> {
	public static final StringParse DEFAULT = new StringParse();

	private final StringEmptyVerification verification;
	private final int numberRadix;
	private final char[] splitArray;

	public StringParse() {
		this(null, 10);
	}

	public StringParse(StringEmptyVerification verification, int numberRadix, char... splitArray) {
		this.verification = verification;
		this.numberRadix = numberRadix;
		this.splitArray = splitArray;
	}

	public boolean verification(CharSequence text) {
		return verification(verification, text);
	}

	public boolean verificationNumberText(String text) {
		if (StringUtils.isEmpty(text)) {
			return true;
		}

		return verification(text);
	}

	public final StringEmptyVerification getVerification() {
		return verification;
	}

	public final int getNumberRadix() {
		return numberRadix;
	}

	public final char[] getSplitArray() {
		return splitArray;
	}

	public String[] split(String text) {
		if (splitArray == null) {
			return StringUtils.commonSplit(text);
		} else {
			return StringUtils.split(text, splitArray);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getArray(String[] arr, Class<T> type) {
		if (ArrayUtils.isEmpty(arr)) {
			return (T[]) Array.newInstance(type, 0);
		}

		Object objects = Array.newInstance(type, arr.length);
		for (int i = 0; i < arr.length; i++) {
			Array.set(objects, i, getObject(arr[i], type));
		}
		return (T[]) objects;
	}

	public static Object defaultParse(String text, Class<?> type) {
		return XUtils.getValue(DEFAULT, text, type);
	}

	public static Object defaultParse(String text, Type type) {
		return XUtils.getValue(DEFAULT, text, type);
	}

	private static boolean verification(Verification<CharSequence> verification, CharSequence charSequence) {
		if (verification == null) {
			return StringEmptyVerification.INSTANCE.verification(charSequence);
		} else {
			return verification.verification(charSequence);
		}
	}

	public Byte getByte(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}
		return Byte.parseByte(text, numberRadix);
	}

	public byte getByteValue(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return 0;
		}
		return Byte.parseByte(text, numberRadix);
	}

	public Short getShort(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}
		return Short.parseShort(text, numberRadix);
	}

	public short getShortValue(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return 0;
		}
		return Short.parseShort(text, numberRadix);
	}

	public Integer getInteger(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}

		return Integer.parseInt(text, numberRadix);
	}

	public int getIntValue(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return 0;
		}

		return Integer.parseInt(text, numberRadix);
	}

	public Long getLong(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}
		return Long.parseLong(text, numberRadix);
	}

	public long getLongValue(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return 0;
		}
		return Long.parseLong(text, numberRadix);
	}

	public Boolean getBoolean(String data) {
		return StringUtils.parseBoolean(data, null);
	}

	public boolean getBooleanValue(String data) {
		return StringUtils.parseBoolean(data);
	}

	public Float getFloat(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}
		return Float.parseFloat(text);
	}

	public float getFloatValue(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return 0f;
		}
		return Float.parseFloat(text);
	}

	public Double getDouble(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}
		return Double.parseDouble(text);
	}

	public double getDoubleValue(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return 0;
		}
		return Double.parseDouble(text);
	}

	public char getChar(String data) {
		if (verificationNumberText(data)) {
			return (char) 0;
		}
		return data.charAt(0);
	}

	public Character getCharacter(String data) {
		if (verificationNumberText(data)) {
			return null;
		}
		return data.charAt(0);
	}

	public String getString(String data) {
		return data;
	}

	public BigInteger getBigInteger(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}

		return new BigInteger(text, numberRadix);
	}

	public BigDecimal getBigDecimal(String data) {
		String text = StringUtils.formatNumberText(data);
		if (verificationNumberText(text)) {
			return null;
		}

		return new BigDecimal(text);
	}

	public Object getObject(String text, Class<?> type) {
		return JSONUtils.parseObject(text, type);
	}

	@SuppressWarnings({ "rawtypes" })
	public Enum<?> getEnum(String data, Class<? extends Enum> enumType) {
		if (verification(data)) {
			return null;
		}

		return EnumUtils.valueOf(enumType, data);
	}

	public <E> E[] getArray(String text, Class<E> type) {
		return getArray(split(text), type);
	}

	public Class<?> getClass(String data) {
		try {
			return ClassUtils.forName(data);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Object getObject(String text, Type type) {
		return JSONUtils.parseObject(text, type);
	}
}
