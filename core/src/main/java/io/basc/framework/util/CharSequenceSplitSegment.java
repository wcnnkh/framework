package io.basc.framework.util;

import java.util.function.Function;

import io.basc.framework.lang.Nullable;

public class CharSequenceSplitSegment implements CharSequence {
	private final CharSequence source;
	@Nullable
	private final CharSequence separator;
	
	public CharSequenceSplitSegment(CharSequence source) {
		this(source, null);
	}

	public CharSequenceSplitSegment(CharSequence source, @Nullable CharSequence separator) {
		Assert.requiredArgument(source != null, "source");
		this.source = source;
		this.separator = separator;
	}
	
	public boolean isLast() {
		return separator == null;
	}

	public CharSequence getSource() {
		return source;
	}

	@Nullable
	public CharSequence getSeparator() {
		return separator;
	}

	@Override
	public int length() {
		return source.length();
	}
	
	public boolean isEmpty() {
		return source.length() == 0;
	}
	
	public CharSequenceSplitSegment trim() {
		return new CharSequenceSplitSegment(StringUtils.trimWhitespace(source), separator);
	}
	
	public CharSequenceSplitSegment map(Function<CharSequence, CharSequence> mapper) {
		return new CharSequenceSplitSegment(mapper.apply(source), separator);
	}

	@Override
	public char charAt(int index) {
		return source.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return source.subSequence(start, end);
	}

	@Override
	public String toString() {
		return source.toString();
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof CharSequenceSplitSegment) {
			return ObjectUtils.equals(source, ((CharSequenceSplitSegment) obj).source);
		}

		return false;
	}
}
