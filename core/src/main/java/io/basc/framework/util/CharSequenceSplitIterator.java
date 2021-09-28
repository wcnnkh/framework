package io.basc.framework.util;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * 迭代分割字符串
 * 
 * @author shuchaowen
 *
 */
public class CharSequenceSplitIterator extends AbstractIterator<CharSequence> {
	private final CharSequence charSequence;
	private final Collection<? extends CharSequence> filters;
	private final int endIndex;
	private int index;
	private Supplier<Pair<Integer, CharSequence>> current;

	CharSequenceSplitIterator(CharSequence charSequence, Collection<? extends CharSequence> filters, int beginIndex,
			int endIndex) {
		this.charSequence = charSequence;
		this.filters = filters;
		this.index = beginIndex;
		this.endIndex = endIndex;
	}

	@Override
	public boolean hasNext() {
		if (index > endIndex) {
			return false;
		}

		if (current == null) {
			for (CharSequence filter : filters) {
				if (filter == null) {
					continue;
				}

				index = StringUtils.indexOf(charSequence, filter, index, endIndex);
				if (index != -1) {
					current = new StaticSupplier<Pair<Integer, CharSequence>>(
							new Pair<Integer, CharSequence>(index, filter));
					break;
				}
			}

			if (current == null) {
				current = new StaticSupplier<Pair<Integer, CharSequence>>(null);
			}
		}

		if (current == null) {
			return false;
		}

		return current.get() != null;
	}

	@Override
	public CharSequence next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		CharSequence value = charSequence.subSequence(index, current.get().getKey());
		index = current.get().getKey() + current.get().getValue().length();
		current = null;
		return value;
	}
}
