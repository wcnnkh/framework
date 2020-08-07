package scw.microsoft.jxl;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import jxl.write.Label;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import scw.microsoft.ExcelException;
import scw.microsoft.WritableSheet;

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
			throw new ExcelException(e);
		} catch (WriteException e) {
			throw new ExcelException(e);
		}
	}
}
