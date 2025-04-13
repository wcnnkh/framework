package run.soeasy.framework.codec.binary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.Encoder;
import run.soeasy.framework.codec.NestedEncoder;

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