package scw.office.excel;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;


public interface ExcelExport extends Closeable{
	void append(String[] row) throws IOException;
	
	void append(Collection<String[]> rows) throws IOException;
}
