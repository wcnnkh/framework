package io.basc.framework.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.support.RecordCodec;
import io.basc.framework.util.Assert;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.CallableProcessor;
import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

/**
 * 线程不安全的
 * 
 * @author wcnnkh
 *
 * @param <T>
 */
public final class FileRecords<T> {
	private final CallableProcessor<File, IOException> lazyCreator;
	private volatile File file;
	private final RecordCodec<T> codec;

	/**
	 * 使用临时文件作为记录器
	 * 
	 * @param codec
	 */
	public FileRecords(Codec<T, byte[]> codec) {
		this(() -> File.createTempFile("records", XUtils.getUUID()), codec);
	}

	public FileRecords(File file, Codec<T, byte[]> codec) {
		this(() -> file, codec);
	}

	public FileRecords(CallableProcessor<File, IOException> lazyCreator, Codec<T, byte[]> codec) {
		Assert.requiredArgument(lazyCreator != null, "lazyCreator");
		Assert.requiredArgument(codec != null, "codec");
		this.lazyCreator = lazyCreator;
		this.codec = new RecordCodec<T>(codec);
	}

	public File getFile() throws IOException {
		if (file == null) {
			synchronized (this) {
				if (file == null) {
					file = lazyCreator.process();
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

	public Cursor<T> stream() {
		if (file != null) {
			synchronized (this) {
				if (file != null) {
					RecordIterator<T> iterator = new RecordIterator<T>(file, codec);
					return StreamProcessorSupport.cursor(iterator).onClose(() -> iterator.close());
				}
			}
		}
		return StreamProcessorSupport.emptyCursor();
	}

	/**
	 * 读取记录
	 * 
	 * @param consumer
	 * @throws E
	 */
	public <E extends Throwable> void consume(ConsumerProcessor<T, E> consumer) throws E {
		Assert.requiredArgument(consumer != null, "consumer");
		if (file != null) {
			synchronized (this) {
				if (file != null) {
					RecordIterator<T> iterator = new RecordIterator<T>(file, codec);
					try {
						while (iterator.hasNext()) {
							consumer.process(iterator.next());
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
