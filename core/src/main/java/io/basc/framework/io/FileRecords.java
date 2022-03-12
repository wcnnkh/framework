package io.basc.framework.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.support.RecordCodec;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.CallableProcessor;
import io.basc.framework.util.stream.ConsumerProcessor;
import io.basc.framework.util.stream.ResponsiveIterator;
import io.basc.framework.util.stream.StreamProcessorSupport;

public final class FileRecords<T> {
	private static final ExecutorService READ_EXECUTOR = Executors.newCachedThreadPool();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				READ_EXECUTOR.shutdown();
			}
		});
	}

	private static Logger logger = LoggerFactory.getLogger(FileRecords.class);
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

	public Stream<T> stream() {
		if (file != null) {
			synchronized (this) {
				if (file != null) {
					ResponsiveIterator<T> iterator = new ResponsiveIterator<T>();
					READ_EXECUTOR.execute(() -> {
						try {
							consume((e) -> iterator.put(e));
						} catch (Throwable e) {
							logger.error(e, "read error");
						} finally {
							try {
								iterator.close();
							} catch (InterruptedException e) {
								logger.error(e, "read thread error");
							}
						}
					});
					return XUtils.stream(iterator);
				}
			}
		}
		return StreamProcessorSupport.emptyStream();
	}

	/**
	 * 读取记录
	 * 
	 * @param consumer
	 * @throws E
	 */
	public <E extends Throwable> void consume(ConsumerProcessor<T, E> consumer) throws E, IOException {
		Assert.requiredArgument(consumer != null, "consumer");
		if (file != null) {
			synchronized (this) {
				if (file != null) {
					FileInputStream fis = new FileInputStream(file);
					try {
						while (true) {
							try {
								T v = codec.decode(fis);
								consumer.process(v);
							} catch (EOFException e) {
								break;
							}
						}
					} finally {
						fis.close();
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
