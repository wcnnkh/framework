package run.soeasy.framework.codec;

public interface MultipleEncoder<E> extends Encoder<E, E> {

	default E encode(E source, int count) throws EncodeException {
		E e = source;
		for (int i = 0; i < count; i++) {
			e = encode(e);
		}
		return e;
	}

	default MultipleEncoder<E> multiple(int count) {
		return new NestedMultipleEncoder<>(this, count);
	}
}
