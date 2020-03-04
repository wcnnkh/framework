package scw.office.excel;

import scw.core.Destroy;

public interface WriteExcel extends Destroy{
	
	void write(String[] contents);

	void write(int rowIndex, String[] contents);

	void write(int sheetIndex, int rowIndex, int colIndex, String content);
	
}
