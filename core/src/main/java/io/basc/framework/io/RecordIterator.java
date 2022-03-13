package io.basc.framework.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.support.RecordCodec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StaticSupplier;
import io.basc.framework.util.stream.CallableProcessor;

/**
 * 线程不安全
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public final class RecordIterator<E> implements Iterator<E>, AutoCloseable {
	private final CallableProcessor<InputStream, IOException> sourceSupplier;
	private final RecordCodec<E> codec;
	private volatile InputStream source;
	private volatile Supplier<E> supplier;

	public RecordIterator(File file, RecordCodec<E> codec) {
		this(() -> new FileInputStream(file), codec);
	}

	public RecordIterator(CallableProcessor<InputStream, IOException> sourceSupplier, RecordCodec<E> codec) {
		Assert.requiredArgument(sourceSupplier != null, "sourceSupplier");
		Assert.requiredArgument(codec != null, "codec");
		this.sourceSupplier = sourceSupplier;
		this.codec = codec;
	}

	private InputStream getSource() throws IOException {
		if (source == null) {
			synchronized (this) {
				if (source == null) {
					source = sourceSupplier.process();
				}
			}
		}
		return source;
	}

	@Override
	public void close() {
		if (source != null) {
			synchronized (this) {
				if (source != null) {
					try {
						IOUtils.closeQuietly(source);
					} finally {
						source = null;
					}
				}
			}
		}
	}

	@Override
	public boolean hasNext() {
		if (supplier == null) {
			try {
				supplier = new StaticSupplier<E>(codec.decode(getSource()));
			} catch (EOFException e) {
				close();
				return false;
			} catch (RuntimeException e) {
				close();
				throw e;
			} catch (Throwable e) {
				close();
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
