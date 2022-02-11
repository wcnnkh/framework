package io.basc.framework.codec;

public interface SimpleMultipleEncoder<E> extends MultipleEncoder<E> {

	@Override
	E encode(E source);

	@Override
	default E encode(E source, int count) throws EncodeException {
		E e = source;
		for (int i = 0; i < count; i++) {
			e = encode(e);
		}
		return e;
	}
}
