package run.soeasy.framework.core.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.stream.Stream;

import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.support.RecordCodec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingSupplier;

/**
 * 线程不安全的
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public final class FileRecords<T> implements Elements<T> {
	private final ThrowingSupplier<? extends File, ? extends IOException> fileSource;
	private volatile File file;
	private final RecordCodec<T> codec;

	public FileRecords(File file, Codec<T, byte[]> codec) {
		this(() -> file, codec);
	}

	public FileRecords(ThrowingSupplier<? extends File, ? extends IOException> fileSource, Codec<T, byte[]> codec) {
		Assert.requiredArgument(fileSource != null, "fileSource");
		Assert.requiredArgument(codec != null, "codec");
		this.fileSource = fileSource;
		this.codec = new RecordCodec<T>(codec);
	}

	public File getFile() throws IOException {
		if (file == null) {
			synchronized (this) {
				if (file == null) {
					file = fileSource.get();
				}
			}
		}
		return file;
	}

	public boolean delete() {
		if (file != null) {
			synchronized (this) {
				if (file != null) {
					try {
						return file.delete();
					} finally {
						this.file = null;
					}

				}
			}
		}
		return false;
	}

	@Override
	public Stream<T> stream() {
		if (file != null) {
			synchronized (this) {
				if (file != null) {
					RecordIterator<T> iterator = new RecordIterator<T>(file, codec);
					return CollectionUtils.unknownSizeStream(iterator);
				}
			}
		}
		return Stream.empty();
	}

	@Override
	public final Iterator<T> iterator() {
		return toList().iterator();
	}

	/**
	 * 读取记录
	 * 
	 * @param consumer
	 * @throws E
	 */
	public <E extends Throwable> void consume(ThrowingConsumer<? super T, ? extends E> consumer) throws E {
		Assert.requiredArgument(consumer != null, "consumer");
		if (file != null) {
			synchronized (this) {
				if (file != null) {
					RecordIterator<T> iterator = new RecordIterator<T>(file, codec);
					try {
						while (iterator.hasNext()) {
							consumer.accept(iterator.next());
						}
					} finally {
						iterator.close();
					}
				}
			}
		}
	}

	public void append(T record) throws IOException {
		if (record == null) {
			return;
		}

		FileOutputStream fos = new FileOutputStream(getFile(), true);
		try {
			codec.encode(record, fos);
		} finally {
			fos.close();
		}
	}
}
