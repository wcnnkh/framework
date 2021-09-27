package io.basc.framework.util;

import java.util.NoSuchElementException;

/**
 * 迭代分割字符串
 * @author shuchaowen
 *
 */
public class CharSequenceSplitIterator extends AbstractIterator<CharSequence>{
	private final CharSequence charSequence;
	private final CharSequence indexCharSequence;
	private int beginIndex;
	private int endIndex;
	private int index = beginIndex;
	private Supplier<Integer> current;
	
	public CharSequenceSplitIterator(CharSequence charSequence, CharSequence indexCharSequence, int beginIndex, int endIndex) {
		this.charSequence = charSequence;
		this.indexCharSequence = indexCharSequence;
		this.beginIndex = beginIndex;
		this.endIndex = endIndex;
	}
	
	@Override
	public boolean hasNext() {
		if(current == null) {
			current = new StaticSupplier<Integer>(StringUtils.indexOf(charSequence, indexCharSequence, index, endIndex));
		}
		return current.get().intValue() != -1;
	}

	@Override
	public CharSequence next() {
		if(!hasNext()) {
			throw new NoSuchElementException();
		}
		
		CharSequence value = charSequence.subSequence(index, current.get());
		current = null;
		index = current.get() + indexCharSequence.length();
		return value;
	}

}
