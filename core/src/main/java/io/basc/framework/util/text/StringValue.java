package io.basc.framework.util.text;

import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.Any;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StringValue implements CharSequenceValue {
	private final CharSequence value;
	private final String delim;

	public StringValue(String value) {
		this(value, null);
	}

	@Override
	public CharSequence getAsCharSequence() {
		return value;
	}

	@Override
	public Elements<? extends Any> getAsElements() {
		return getAsElements(this.delim);
	}

	public Elements<? extends Any> getAsElements(String delim) {
		if (value == null) {
			return Elements.empty();
		}
		return StringUtils.split(value, delim).map((e) -> new StringValue(value, delim));
	}

	@Override
	public boolean isMultiple() {
		return delim != null;
	}

	@Override
	public int length() {
		return value == null ? 0 : value.length();
	}
}
