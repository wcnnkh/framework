package io.basc.framework.microsoft;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.util.AbstractIterator;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.stream.Cursor;

public interface Excel extends Closeable {
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
		return stream().filter((s) -> s != null && StringUtils.equals(sheetName, s.getName())).first();
	}

	/**
	 * 操作所有的sheet
	 * 
	 * @return
	 */
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

		Cursor<? extends Sheet> cursor = new Cursor<>(XUtils.stream(iterator).onClose(() -> {
			try {
				close();
			} catch (IOException e) {
				throw new NestedRuntimeException(Excel.this.toString(), e);
			}
		}));
		cursor.setAutoClose(false);
		return cursor;
	}
}
