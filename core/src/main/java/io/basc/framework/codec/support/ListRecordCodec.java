package io.basc.framework.codec.support;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.util.Assert;

public class ListRecordCodec<D> implements ToBytesCodec<List<D>> {
	private final RecordCodec<D> codec;

	public ListRecordCodec(Codec<D, byte[]> codec) {
		Assert.requiredArgument(codec != null, "codec");
		this.codec = new RecordCodec<D>(codec);
	}

	@Override
	public void encode(List<D> source, OutputStream target) throws IOException, EncodeException {
		target.write(source == null ? 0 : source.size());
		if (source != null) {
			for (D d : source) {
				codec.encode(d, target);
			}
		}
	}

	@Override
	public List<D> decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		int size = source.read();
		if (size == -1) {
			throw new EOFException();
		}

		if (size == 0) {
			return Collections.emptyList();
		}

		List<D> list = new ArrayList<D>(size);
		for (int i = 0; i < size; i++) {
			list.add(codec.decode(source, bufferSize));
		}
		return list;
	}

}
