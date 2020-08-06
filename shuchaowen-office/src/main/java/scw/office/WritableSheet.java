package scw.office;

import java.io.IOException;
import java.util.Collection;

public interface WritableSheet extends Sheet {
	void write(int rowIndex, Collection<String> contents) throws IOException, ExcelException;

	void write(int rowIndex, int colIndex, String content) throws IOException, ExcelException;
}
