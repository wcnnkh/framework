package scw.office.excel;

public interface RowCallback {
	void call(int sheetIndex, int rowIndex, String[] contents);
}
