package io.basc.framework.microsoft;

import java.io.IOException;
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
	public Stream<String[]> read(Object source) throws IOException, ExcelException {
		ResponsiveIterator<String[]> iterator = new ResponsiveIterator<String[]>();
		RowCallback callback = (sheetIndex, rowIndex, contents) -> {
			try {
				iterator.put(contents);
			} catch (InterruptedException e) {
				logger.error(e, "put sheetIndex={}, rowIndex={}, contents={} error", sheetIndex, rowIndex, contents);
			}
		};

		READ_EXECUTOR.execute(() -> {
			try {
				read(source, callback);
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
