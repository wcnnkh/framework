package scw.microsoft;

import java.io.Closeable;

public interface Excel extends Closeable {
	Sheet getSheet(int sheetIndex);

	Sheet getSheet(String sheetName);

	int getNumberOfSheets();
}
