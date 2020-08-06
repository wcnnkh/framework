package scw.office;

import java.io.IOException;
import java.util.Collection;

public class ExcelExportWrapper implements ExcelExport {
	private final ExcelExport excelExport;

	public ExcelExportWrapper(ExcelExport excelExport) {
		this.excelExport = excelExport;
	}

	public void flush() throws IOException {
		this.excelExport.flush();
	}

	public void close() throws IOException {
		this.excelExport.close();
	}

	public void append(Collection<String> contents) throws IOException, ExcelException {
		this.excelExport.append(contents);
	}

	public void append(String... contents) throws IOException, ExcelException {
		this.excelExport.append(contents);
	}
}
