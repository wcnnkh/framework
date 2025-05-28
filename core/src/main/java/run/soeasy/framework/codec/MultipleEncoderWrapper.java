package run.soeasy.framework.codec;
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