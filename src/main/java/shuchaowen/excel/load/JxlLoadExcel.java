package shuchaowen.excel.load;

import java.io.File;

public class JxlLoadExcel extends AbstractJxlLoadExcel{
	private final LoadRow loadRow;
	
	public JxlLoadExcel(File excel, LoadRow loadRow) {
		super(excel);
		this.loadRow = loadRow;
	}

	public void load(int sheetIndex, int rowIndex, String[] contents) {
		loadRow.load(sheetIndex, rowIndex, contents);
	}
}
