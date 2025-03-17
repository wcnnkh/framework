package run.soeasy.framework.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.math.BigDecimalValue;
import run.soeasy.framework.util.math.NumberValue;

/**
 * 字符序列模板
 * 
 * @author shuchaowen
 *
 */
@Data
@EqualsAndHashCode(of = "value")
@ToString(of = "value")
public class CharSequenceTemplate implements CharSequence, Version, Serializable {
	private static final long serialVersionUID = 1L;
	private final CharSequence value;
	private final CharSequence delimiter;

	public CharSequenceTemplate(@NonNull CharSequence value) {
		this(value, null);
	}

	public CharSequenceTemplate(@NonNull CharSequence value, CharSequence delimiter) {
		this.value = value;
		this.delimiter = delimiter;
	}

	@Override
	public char charAt(int index) {
		return value.charAt(index);
	}

	@Override
	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(getAsString());
	}

	@Override
	public BigInteger getAsBigInteger() {
		return new BigInteger(getAsString());
	}

	@Override
	public boolean getAsBoolean() {
		String value = getAsString();
		return value == null ? false : Boolean.parseBoolean(value);
	}

	@Override
	public byte getAsByte() {
		String value = getAsString();
		return value == null ? 0 : Byte.parseByte(value);
	}

	@Override
	public char getAsChar() {
		return charAt(0);
	}

	@Override
	public double getAsDouble() {
		String value = getAsString();
		return value == null ? 0 : Double.parseDouble(value);
	}

	@Override
	public Elements<? extends CharSequenceTemplate> getAsElements() {
		if (delimiter == null) {
			return Elements.singleton(this);
		}
		return getAsElements(this.delimiter);
	}

	public Elements<? extends CharSequenceTemplate> getAsElements(CharSequence delimiter) {
		if (value == null) {
			return Elements.empty();
		}
		return StringUtils.split(value, delimiter).map((e) -> new CharSequenceTemplate(value, delimiter));
	}

	@Override
	public <T extends Enum<T>> T getAsEnum(Class<T> enumType) throws IllegalArgumentException, NullPointerException {
		String value = getAsString();
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return Enum.valueOf(enumType, value);
	}

	@Override
	public float getAsFloat() {
		String value = getAsString();
		return value == null ? 0 : Float.parseFloat(value);
	}

	@Override
	public int getAsInt() {
		String value = getAsString();
		return value == null ? 0 : Integer.parseInt(value);
	}

	@Override
	public long getAsLong() {
		String value = getAsString();
		return value == null ? 0 : Long.parseLong(value);
	}

	@Override
	public NumberValue getAsNumber() throws NumberFormatException {
		return new BigDecimalValue(getAsString());
	}

	@Override
	public short getAsShort() {
		String value = getAsString();
		return value == null ? 0 : Short.parseShort(value);
	}

	@Override
	public String getAsString() {
		return value.toString();
	}

	@Override
	public Version getAsVersion() {
		return this;
	}

	@Override
	public boolean isMultiple() {
		return delimiter != null;
	}

	@Override
	public boolean isNumber() {
		try {
			getAsNumber();
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public int length() {
		return value == null ? 0 : value.length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return value.subSequence(start, end);
	}

	public CharSequenceTemplate trim() {
		return new CharSequenceTemplate(value.toString().trim(), delimiter);
	}

	@Override
	public Version join(@NonNull Version version) {
		Elements<Version> elements = getAsElements().map(Function.identity());
		elements = elements.concat(Elements.singleton(version));
		return new JoinVersion(elements, delimiter);
	}
}
