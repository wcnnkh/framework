package io.basc.framework.codec;

import io.basc.framework.util.Validator;

public final class CodecUtils {
	private CodecUtils() {
	}

	public static <D, E> Signer<D, E> build(Encoder<D, E> encoder, Validator<D, E> validator) {
		return new Signer<D, E>() {

			@Override
			public boolean verify(D source, E encode) throws CodecException {
				return validator.verify(source, encode);
			}

			@Override
			public E encode(D source) throws EncodeException {
				return encoder.encode(source);
			}
		};
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

	public static <T> MultipleCodec<T> build(Codec<T, T> codec) {
		return new MultipleCodec<T>() {

			@Override
			public T encode(T source) throws EncodeException {
				return codec.encode(source);
			}

			@Override
			public T decode(T source) throws DecodeException {
				return codec.decode(source);
			}
		};
	}

	public static <D> MultipleDecoder<D> build(Decoder<D, D> decoder) {
		return new MultipleDecoder<D>() {

			@Override
			public D decode(D source) throws DecodeException {
				return decoder.decode(source);
			}
		};
	}

	public static <E> MultipleEncoder<E> build(Encoder<E, E> encoder) {
		return new MultipleEncoder<E>() {

			@Override
			public E encode(E source) throws EncodeException {
				return encoder.encode(source);
			}
		};
	}
}
