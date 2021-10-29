package io.basc.framework.microsoft;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Cursor;

public interface Excel extends Closeable {
	static final Logger logger = LoggerFactory.getLogger(Excel.class);

	/**
	 * 获取指定索引的sheet
	 * 
	 * @param sheetIndex 从0开始
	 * @return
	 */
	Sheet getSheet(int sheetIndex);

	/**
	 * 获取有多少个sheet
	 * 
	 * @return
	 */
	int getNumberOfSheets();

	default Sheet getSheet(String sheetName) {
		for (int i = 0; i < getNumberOfSheets(); i++) {
			Sheet sheet = getSheet(i);
			if (sheet == null) {
				continue;
			}

			if (StringUtils.equals(sheetName, sheet.getName())) {
				return sheet;
			}
		}
		return null;
	}

	/**
	 * 操作所有的sheet
	 * 
	 * @return
	 */
	@SuppressWarnings("resource")
	default Cursor<? extends Sheet> stream() {
		Iterator<Sheet> iterator = new AbstractIterator<Sheet>() {
			private int index = 0;
			private int count = getNumberOfSheets();

			@Override
			public boolean hasNext() {
				return index < count;
			}

			@Override
			public Sheet next() {
				return getSheet(index++);
			}
		};
		Cursor<Sheet> cursor = new Cursor<>(iterator);
		cursor.setAutoClose(false);
		return cursor.onClose(() -> {
			try {
				close();
			} catch (IOException e) {
				logger.error(e, "close excel[{}] error", Excel.this.toString());
			}
		});
	}
}
