package scw.office;

import java.io.Flushable;

public interface WritableExcel extends Excel, Flushable {
	WritableSheet[] getSheets();

	WritableSheet getSheet(int sheetIndex);

	WritableSheet getSheet(String sheetName);

	WritableSheet createSheet(String sheetName, int sheetIndex);
}
