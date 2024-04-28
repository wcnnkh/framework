package io.basc.framework.jxl;

import java.io.IOException;

import io.basc.framework.excel.ExcelException;
import io.basc.framework.excel.WritableSheet;
import io.basc.framework.value.Value;
import jxl.write.Label;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class JxlWritableSheet extends JxlSheet implements WritableSheet {

	public JxlWritableSheet(jxl.write.WritableSheet sheet, int positionIndex) {
		super(sheet, positionIndex);
	}

	@Override
	public jxl.write.WritableSheet getSheet() {
		return (jxl.write.WritableSheet) super.getSheet();
	}

	@Override
	public void flush() throws IOException {
		// ignore
	}

	@Override
	public void doWriteColumn(int rowIndex, int colIndex, Value column) throws IOException, ExcelException {
		try {
			getSheet().addCell(new Label(colIndex, rowIndex, column.getAsString()));
		} catch (RowsExceededException e) {
			throw new ExcelException("write row=" + rowIndex + ", col=" + colIndex, e);
		} catch (WriteException e) {
			throw new ExcelException("write row=" + rowIndex + ", col=" + colIndex, e);
		}
	}
}
