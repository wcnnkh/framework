package io.basc.framework.codec;

import io.basc.framework.util.Wrapper;

public class NestedMultipleDecoder<W extends MultipleDecoder<D>, D> extends Wrapper<W> implements MultipleDecoder<D> {
	private final int count;

	public NestedMultipleDecoder(W decoder, int count) {
		super(decoder);
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public D decode(D source) throws DecodeException {
		return wrappedTarget.decode(source, count);
	}

	@Override
	public D decode(D source, int count) throws DecodeException {
		return wrappedTarget.decode(source, count);
	}
}
