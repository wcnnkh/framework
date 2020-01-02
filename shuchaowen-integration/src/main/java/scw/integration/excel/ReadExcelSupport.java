package scw.integration.excel;

import java.io.InputStream;

public interface ReadExcelSupport {
	void read(String excel, RowCallback callback);

	void read(InputStream inputStream, RowCallback callback);
}
