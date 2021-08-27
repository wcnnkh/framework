package io.basc.framework.microsoft.jxl;

import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.WritableSheet;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import jxl.write.Label;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class JxlWritableSheet extends JxlSheet implements WritableSheet {

	public JxlWritableSheet(jxl.write.WritableSheet sheet) {
		super(sheet);
	}

	@Override
	public jxl.write.WritableSheet getSheet() {
		return (jxl.write.WritableSheet) super.getSheet();
	}

	public void write(int rowIndex, Collection<String> contents) throws IOException, ExcelException {
		Iterator<String> iterator = contents.iterator();
		int colIndex = 0;
		while (iterator.hasNext()) {
			write(rowIndex, colIndex++, iterator.next());
		}
	}

	public void write(int rowIndex, int colIndex, String content) throws IOException, ExcelException {
		try {
			getSheet().addCell(new Label(colIndex, rowIndex, content));
		} catch (RowsExceededException e) {
			throw new ExcelException("write row=" + rowIndex + ", col=" + colIndex, e);
		} catch (WriteException e) {
			throw new ExcelException("write row=" + rowIndex + ", col=" + colIndex, e);
		}
	}
}
