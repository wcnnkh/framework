package scw.microsoft;

@FunctionalInterface
public interface RowCallback {
	void processRow(int sheetIndex, int rowIndex, String[] contents);
}
