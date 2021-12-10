package io.basc.framework.util;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * 迭代分割字符串
 * 
 * @author shuchaowen
 *
 */
public class CharSequenceSplitIterator extends AbstractIterator<CharSequenceSplitSegment> {
	private final CharSequence charSequence;
	private final Collection<? extends CharSequence> filters;
	private final int endIndex;
	private int index;
	private Supplier<Pair<Integer, CharSequence>> current;

	public CharSequenceSplitIterator(CharSequence charSequence, Collection<? extends CharSequence> filters,
			int beginIndex, int endIndex) {
		this.charSequence = charSequence;
		this.filters = filters;
		this.index = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public boolean hasNext() {
		if (index >= endIndex) {
			return false;
		}

		if (current == null) {
			for (CharSequence filter : filters) {
				if (filter == null) {
					continue;
				}

				int index = StringUtils.indexOf(charSequence, filter, this.index, endIndex);
				if (index != -1) {
					current = new StaticSupplier<Pair<Integer, CharSequence>>(
							new Pair<Integer, CharSequence>(index, filter));
					break;
				}
			}
		}

		if (current == null) {
			return index < endIndex;
		}
		return true;
	}

	@Override
	public CharSequenceSplitSegment next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		if (current == null) {
			// 最后一次了
			CharSequence value = index == 0 ? charSequence : charSequence.subSequence(index, endIndex);
			index = endIndex;
			return new CharSequenceSplitSegment(value);
		}

		CharSequence value = charSequence.subSequence(index, current.get().getKey());
		index = current.get().getKey() + current.get().getValue().length();
		CharSequenceSplitSegment pair = new CharSequenceSplitSegment(value, current.get().getValue());
		current = null;
		return pair;
	}
}
