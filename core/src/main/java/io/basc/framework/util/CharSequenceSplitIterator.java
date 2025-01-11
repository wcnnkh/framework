package io.basc.framework.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.util.function.Functions;

/**
 * 迭代分割字符串
 * 
 * @author wcnnkh
 *
 */
public class CharSequenceSplitIterator implements Iterator<CharSequenceTemplate> {
	private final CharSequence charSequence;
	private final Collection<? extends CharSequence> filters;
	private final int endIndex;
	private int index;
	private Supplier<KeyValue<Integer, CharSequence>> current;

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
					current = Functions.toSupplier(KeyValue.of(index, filter));
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
	public CharSequenceTemplate next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		if (current == null) {
			// 最后一次了
			CharSequence value = index == 0 ? charSequence : charSequence.subSequence(index, endIndex);
			index = endIndex;
			return new CharSequenceTemplate(value);
		}

		CharSequence value = charSequence.subSequence(index, current.get().getKey());
		index = current.get().getKey() + current.get().getValue().length();
		CharSequenceTemplate pair = new CharSequenceTemplate(value, current.get().getValue());
		current = null;
		return pair;
	}
}
