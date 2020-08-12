package scw.microsoft;

public interface RowCallback {
	void processRow(int sheetIndex, int rowIndex, String[] contents);
}
