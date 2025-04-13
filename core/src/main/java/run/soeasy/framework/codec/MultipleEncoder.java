package run.soeasy.framework.codec;

public interface MultipleEncoder<E> extends Encoder<E, E> {
	public interface MultipleEncoderWrapper<E, W extends MultipleEncoder<E>>
			extends MultipleEncoder<E>, EncoderWrapper<E, E, W> {
		@Override
		default E encode(E source, int count) throws EncodeException {
			return getSource().encode(source, count);
		}

		@Override
		default MultipleEncoder<E> multiple(int count) {
			return getSource().multiple(count);
		}
	}

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
