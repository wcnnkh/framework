package io.basc.framework.jxl;

import java.io.IOException;

import io.basc.framework.excel.ExcelException;
import io.basc.framework.excel.WritableSheet;
import io.basc.framework.mapper.io.table.Column;
import io.basc.framework.mapper.io.table.TableExporter;
import jxl.write.Label;
import jxl.write.WriteException;

public class JxlWritableSheet extends JxlSheet implements WritableSheet, TableExporter {

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
	public void doWriteColumn(Column column) throws IOException {
		Label label = new Label(column.getPositionIndex(), column.getRow().getPositionIndex(), column.getAsString());
		try {
			getSheet().addCell(label);
		} catch (WriteException e) {
			throw new ExcelException(
					"write row=" + column.getRow().getPositionIndex() + ", col=" + column.getPositionIndex(), e);
		}
	}
}
