package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Streams;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ResponsiveIterator;

public abstract class ResponsiveExcelReader implements ExcelReader {
	private static Logger logger = LoggerFactory.getLogger(ResponsiveExcelReader.class);
	private static final Executor READ_EXECUTOR = XUtils.getCommonExecutor();

	@Override
	public Elements<ExcelRow> read(File source) throws IOException, ExcelException {
		return Elements.of(() -> {
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
					iterator.close();
				}
			});
			return Streams.stream(iterator);
		});
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
				iterator.close();
			}
		});
		return Streams.stream(iterator);
	}
}
