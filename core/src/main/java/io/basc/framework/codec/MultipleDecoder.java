package io.basc.framework.codec;

public interface MultipleDecoder<D> extends Decoder<D, D> {

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
