package io.basc.framework.codec;

public final class CodecUtils {
	private CodecUtils() {
	}

	public static <D, E> Codec<D, E> build(Encoder<D, E> encoder, Decoder<E, D> decoder) {
		return new Codec<D, E>() {

			@Override
			public E encode(D source) throws EncodeException {
				return encoder.encode(source);
			}

			@Override
			public D decode(E source) throws DecodeException {
				return decoder.decode(source);
			}
		};
	}

	public static <E> E encode(Encoder<E, E> encoder, E source, int count) {
		E e = source;
		for (int i = 0; i < count; i++) {
			e = encoder.encode(e);
		}
		return e;
	}

	public static <D> D decode(Decoder<D, D> decoder, D source, int count) {
		D d = source;
		for (int i = 0; i < count; i++) {
			d = decoder.decode(d);
		}
		return d;
	}
}
