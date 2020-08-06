package scw.office.jxl;

import java.io.IOException;

import jxl.Cell;

public class JxlSheet implements scw.office.Sheet {
	private final jxl.Sheet sheet;

	public JxlSheet(jxl.Sheet sheet) {
		this.sheet = sheet;
	}

	public String getName() {
		return sheet.getName();
	}

	public jxl.Sheet getSheet() {
		return sheet;
	}

	public String[] read(int rowIndex) throws IOException {
		String[] contents = new String[sheet.getColumns()];
		for (int colIndex = 0; colIndex < contents.length; colIndex++) {
			contents[colIndex] = read(rowIndex, colIndex);
		}
		return contents;
	}

	public String read(int rowIndex, int colIndex) throws IOException {
		Cell cell = sheet.getCell(colIndex, rowIndex);
		return cell.getContents();
	}

	public int getColumns() {
		return sheet.getColumns();
	}

	public int getRows() {
		return sheet.getRows();
	}
}
