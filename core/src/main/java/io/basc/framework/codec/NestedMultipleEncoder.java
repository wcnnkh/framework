package io.basc.framework.codec;

import io.basc.framework.util.Wrapper;

public class NestedMultipleEncoder<W extends MultipleEncoder<E>, E> extends Wrapper<W> implements MultipleEncoder<E> {
	private final int count;

	public NestedMultipleEncoder(W encoder, int count) {
		super(encoder);
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public E encode(E source) throws EncodeException {
		return wrappedTarget.encode(source, count);
	}

	@Override
	public E encode(E encode, int count) throws EncodeException {
		return wrappedTarget.encode(encode, count);
	}
}
