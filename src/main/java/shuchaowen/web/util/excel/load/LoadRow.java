package shuchaowen.web.util.excel.load;

public interface LoadRow {
	public void load(int sheetIndex, int rowIndex, String[] contents);
}
