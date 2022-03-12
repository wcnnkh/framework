package io.basc.framework.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.support.RecordCodec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StaticSupplier;

/**
 * 线程不安全
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public class RecordIterator<E> implements Iterator<E> {
	private final InputStream source;
	private final RecordCodec<E> codec;
	private volatile Supplier<E> supplier;

	public RecordIterator(InputStream source, RecordCodec<E> codec) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(codec != null, "codec");
		this.source = source;
		this.codec = codec;
	}

	@Override
	public boolean hasNext() {
		if (supplier == null) {
			try {
				supplier = new StaticSupplier<E>(codec.decode(source));
			} catch (EOFException e) {
				return false;
			} catch (IOException e) {
				throw new DecodeException(e);
			}
		}
		return true;
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		try {
			return supplier.get();
		} finally {
			this.supplier = null;
		}
	}
}
