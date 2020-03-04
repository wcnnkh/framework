package scw.office.excel.jxl.load;

public interface LoadRow {
	void load(int sheetIndex, int rowIndex, String[] contents);
}
