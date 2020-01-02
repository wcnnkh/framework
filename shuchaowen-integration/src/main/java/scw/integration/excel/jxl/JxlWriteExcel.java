package scw.integration.excel.jxl;

import java.io.IOException;
import java.io.OutputStream;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import scw.integration.excel.WriteExcel;

public class JxlWriteExcel implements WriteExcel {
	private WritableWorkbook workbook;
	private String sheetNamePrefix;
	// 创建Excel工作薄
	private int rowIndex = 0;
	private int maxCount = 60000;// 一个sheet最多放多少数据
	private int sheetIndex = 1;

	public JxlWriteExcel(OutputStream outputStream) {
		try {
			this.workbook = Workbook.createWorkbook(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String[] contents) {
		write(rowIndex, contents);
	}

	public void write(int rowIndex, String[] contents) {
		if (contents == null || contents.length == 0) {
			return;
		}

		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null) {
				continue;
			}

			write(sheetIndex, rowIndex, i, contents[i]);
		}

		this.rowIndex = rowIndex++;
		if (this.rowIndex > maxCount) {
			this.rowIndex = 0;
			sheetIndex++;
		}
	}

	public void write(int sheetIndex, int rowIndex, int colIndex, String content) {
		WritableSheet sheet = workbook.getSheet(sheetIndex);
		if (sheet == null) {
			sheet = workbook.createSheet(sheetNamePrefix + sheetIndex, sheetIndex); // sheet名称
		}

		try {
			sheet.addCell(new Label(colIndex, rowIndex, content));
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		if (workbook == null) {
			try {
				workbook.write();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
