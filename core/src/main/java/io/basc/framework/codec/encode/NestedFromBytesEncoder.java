package io.basc.framework.codec.encode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.Encoder;
import io.basc.framework.codec.NestedEncoder;

public class NestedFromBytesEncoder<P extends FromBytesEncoder<T>, DE extends Encoder<T, E>, T, E>
		extends NestedEncoder<P, DE, byte[], T, E> implements FromBytesEncoder<E> {

	public NestedFromBytesEncoder(P parent, DE decoder) {
		super(parent, decoder);
	}

	@Override
	public E encode(File source) throws IOException, EncodeException {
		T t = parent.encode(source);
		return encoder.encode(t);
	}

	@Override
	public E encode(InputStream source) throws IOException, DecodeException {
		T t = parent.encode(source);
		return encoder.encode(t);
	}

	@Override
	public E encode(InputStream source, int bufferSize) throws IOException, DecodeException {
		T t = parent.encode(source, bufferSize);
		return encoder.encode(t);
	}
}