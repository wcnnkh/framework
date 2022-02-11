package io.basc.framework.codec;

public interface SimpleMultipleDecoder<D> extends MultipleDecoder<D> {
	@Override
	default D decode(D source, int count) throws DecodeException {
		D d = source;
		for (int i = 0; i < count; i++) {
			d = decode(d);
		}
		return d;
	}

	@Override
	D decode(D source) throws DecodeException;
}
