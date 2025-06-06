package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.function.Function;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.math.BigDecimalValue;
import run.soeasy.framework.core.math.NumberValue;

/**
 * 字符序列模板
 * 
 * @author shuchaowen
 *
 */
@Data
@EqualsAndHashCode(of = "value")
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
	public NumberValue getAsNumber() throws NumberFormatException {
		return new BigDecimalValue(getAsString());
	}

	@Override
	public String getAsString() {
		return value.toString();
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
	public CharSequenceTemplate subSequence(int start, int end) {
		CharSequence subSequence = value.subSequence(start, end);
		return new CharSequenceTemplate(subSequence, delimiter);
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

	@Override
	public String toString() {
		return getAsString();
	}
}
