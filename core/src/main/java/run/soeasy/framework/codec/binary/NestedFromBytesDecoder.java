package run.soeasy.framework.codec.binary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.codec.NestedDecoder;

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