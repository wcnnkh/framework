package io.basc.framework.util.codec.decode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.Decoder;
import io.basc.framework.util.codec.NestedDecoder;

public class NestedFromBytesDecoder<P extends FromBytesDecoder<T>, DE extends Decoder<T, E>, T, E>
		extends NestedDecoder<P, DE, byte[], T, E> implements FromBytesDecoder<E> {

	public NestedFromBytesDecoder(P parent, DE decoder) {
		super(parent, decoder);
	}

	@Override
	public E decode(InputStream source) throws IOException, DecodeException {
		T t = parent.decode(source);
		return decoder.decode(t);
	}

	@Override
	public E decode(File source) throws IOException, DecodeException {
		T t = parent.decode(source);
		return decoder.decode(t);
	}

	@Override
	public E decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		T t = parent.decode(source, bufferSize);
		return decoder.decode(t);
	}
}