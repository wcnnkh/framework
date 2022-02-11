package io.basc.framework.codec;

import io.basc.framework.util.Wrapper;

public class NestedMultipleCodec<W extends MultipleCodec<T>, T> extends Wrapper<W> implements MultipleCodec<T> {
	private final int count;

	public NestedMultipleCodec(W wrappedTarget, int count) {
		super(wrappedTarget);
		this.count = count;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public T encode(T source) throws EncodeException {
		return wrappedTarget.encode(source, count);
	}

	@Override
	public T decode(T source) throws DecodeException {
		return wrappedTarget.decode(source, count);
	}

	@Override
	public T encode(T encode, int count) throws EncodeException {
		return wrappedTarget.encode(encode, count);
	}

	@Override
	public T decode(T source, int count) throws DecodeException {
		return wrappedTarget.decode(source, count);
	}

}
