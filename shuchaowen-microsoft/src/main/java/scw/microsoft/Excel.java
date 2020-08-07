package scw.microsoft;

import java.io.Closeable;

public interface Excel extends Closeable {
	Sheet[] getSheets();

	Sheet getSheet(int sheetIndex);

	Sheet getSheet(String sheetName);
}
