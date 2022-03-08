package io.basc.framework.microsoft;

import java.io.Closeable;
import java.util.Arrays;
import java.util.stream.Stream;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

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

	default Sheet[] getSheets() {
		Sheet[] sheets = new Sheet[getNumberOfSheets()];
		for (int i = 0; i < sheets.length; i++) {
			sheets[i] = getSheet(i);
		}
		return sheets;
	}

	/**
	 * 所有数据
	 * 
	 * @return
	 */
	default Cursor<String[]> stream() {
		Stream<String[]> stream = StreamProcessorSupport
				.concat(Arrays.asList(getSheets()).stream().map((e) -> e.stream()).iterator());
		return StreamProcessorSupport.cursor(stream);
	}
}
