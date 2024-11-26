package io.basc.framework.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import io.basc.framework.util.Assert;
import io.basc.framework.util.CloseableIterator;
import io.basc.framework.util.Source;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.support.RecordCodec;
import io.basc.framework.util.function.StaticSupplier;

/**
 * 线程不安全
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public final class RecordIterator<E> implements CloseableIterator<E> {
	private final Source<? extends InputStream, ? extends IOException> source;
	private final RecordCodec<E> codec;
	private volatile InputStream inputStream;
	private volatile Supplier<E> supplier;

	public RecordIterator(File file, RecordCodec<E> codec) {
		this(() -> new FileInputStream(file), codec);
	}

	public RecordIterator(Source<? extends InputStream, ? extends IOException> source, RecordCodec<E> codec) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(codec != null, "codec");
		this.source = source;
		this.codec = codec;
	}

	private InputStream getInputStream() throws IOException {
		if (inputStream == null) {
			synchronized (this) {
				if (inputStream == null) {
					inputStream = source.get();
				}
			}
		}
		return inputStream;
	}

	@Override
	public void close() {
		if (inputStream != null) {
			synchronized (this) {
				if (inputStream != null) {
					try {
						IOUtils.closeQuietly(inputStream);
					} finally {
						inputStream = null;
					}
				}
			}
		}
	}

	@Override
	public boolean hasNext() {
		if (supplier == null) {
			try {
				supplier = new StaticSupplier<E>(codec.decode(getInputStream()));
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
