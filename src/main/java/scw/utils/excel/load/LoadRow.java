package scw.utils.excel.load;

public interface LoadRow {
	public void load(int sheetIndex, int rowIndex, String[] contents);
}
