package run.soeasy.framework.codec;

public interface MultipleDecoder<D> extends Decoder<D, D> {
	public static interface MultipleDecoderWrapper<D, W extends MultipleDecoder<D>>
			extends MultipleDecoder<D>, DecoderWrapper<D, D, W> {
		@Override
		default D decode(D source, int count) throws DecodeException {
			return getSource().decode(source, count);
		}

		@Override
		default MultipleDecoder<D> multiple(int count) {
			return getSource().multiple(count);
		}
	}

	default D decode(D source, int count) throws DecodeException {
		D v = source;
		for (int i = 0; i < count; i++) {
			v = decode(v);
		}
		return v;
	}

	default MultipleDecoder<D> multiple(int count) {
		return new NestedMultipleDecoder<>(this, count);
	}
}
