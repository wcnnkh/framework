package io.basc.framework.microsoft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.basc.framework.util.page.PageSupport;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public interface Sheet extends Paginations<String[]> {
	String getName();

	/**
	 * 读取指定行
	 * 
	 * @param rowIndex 从0开始
	 * @return
	 * @throws IOException
	 * @throws ExcelException
	 */
	String[] read(int rowIndex) throws IOException, ExcelException;

	/**
	 * 读取指定行，指定列
	 * 
	 * @param rowIndex 从0开始
	 * @param colIndex 从0开始
	 * @return
	 * @throws IOException
	 * @throws ExcelException
	 */
	String read(int rowIndex, int colIndex) throws IOException, ExcelException;

	/**
	 * 如果支持流读取应该重写此方法
	 */
	default Cursor<String[]> stream() {
		List<String[]> list = new ArrayList<String[]>();
		for (long rowIndex = getCursorId(); rowIndex < Math.min(Integer.MIN_VALUE,
				Math.min(getCursorId() + getCount(), getTotal())); rowIndex++) {
			try {
				String[] contents = read((int) rowIndex);
				list.add(contents);
			} catch (IOException e) {
				throw new ExcelException(e);
			}
		}
		return StreamProcessorSupport.cursor(list.iterator());
	}

	@Override
	Sheet jumpTo(Long cursorId, long count);

	@Override
	default List<String[]> getList() {
		return stream().collect(Collectors.toList());
	}

	@Override
	default Sheet jumpToPage(long pageNumber) {
		return jumpToPage(pageNumber, getCount());
	}

	@Override
	default Sheet jumpTo(Long cursorId) {
		return jumpTo(cursorId, getCount());
	}

	@Override
	default Sheet jumpToPage(long pageNumber, long count) {
		return jumpTo(PageSupport.getStart(pageNumber, count), count);
	}

	@Override
	default Sheet next() {
		return jumpTo(getNextCursorId());
	}

	@Override
	default Sheet previous() {
		return jumpToPage(getPageNumber() - 1);
	}
}
