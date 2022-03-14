package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.ResponsiveIterator;

public abstract class AbstractExcelReader implements ExcelReader {
	private static Logger logger = LoggerFactory.getLogger(AbstractExcelReader.class);
	private static final ExecutorService READ_EXECUTOR = Executors.newCachedThreadPool();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				READ_EXECUTOR.shutdown();
			}
		});
	}

	@Override
	public Stream<ExcelRow> read(File source) throws IOException, ExcelException {
		ResponsiveIterator<ExcelRow> iterator = new ResponsiveIterator<>();
		READ_EXECUTOR.execute(() -> {
			try {
				read(source, (row) -> {
					try {
						iterator.put(row);
					} catch (InterruptedException e) {
						logger.error(e, "put row: {}", row);
					}
				});
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

	@Override
	public Stream<ExcelRow> read(InputStream source) throws IOException, ExcelException {
		ResponsiveIterator<ExcelRow> iterator = new ResponsiveIterator<>();
		READ_EXECUTOR.execute(() -> {
			try {
				read(source, (row) -> {
					try {
						iterator.put(row);
					} catch (InterruptedException e) {
						logger.error(e, "put row: {}", row);
					}
				});
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
