package scw.excel.export;

import java.sql.ResultSet;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import scw.db.result.Result;

public class ResultSetToExeclRowCall {
	// 创建Excel工作薄
	private int tempSize = 0;
	private int maxCount = 60000;// 一个sheet最多放多少数据
	private WritableSheet sheet;
	private int sheetIndex = 1;
	private Label label;
	private String[] title;
	private WritableWorkbook wwb;
	private SqlExportRow exportRow;

	public ResultSetToExeclRowCall(WritableWorkbook wwb, String[] title,
			SqlExportRow exportRow) {
		this.wwb = wwb;
		this.title = title;
		this.exportRow = exportRow;
	}

	public void format(ResultSet resultSet) throws Exception {
		if (sheet == null) {
			sheet = wwb.createSheet("sheet" + sheetIndex, sheetIndex - 1);
		}

		if (tempSize == 0) {
			// Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
			for (int i = 0; i < title.length; i++) {
				// 在Label对象的子对象中指明单元格的位置和内容
				label = new Label(i, 0, title[i]);
				// 将定义好的单元格添加到工作表中
				sheet.addCell(label);
			}
			tempSize++;
		}

		while (resultSet.next()) {
			Result result = new Result(resultSet);
			String[] contents = exportRow.exportRow(result);
			if (contents == null || contents.length == 0) {
				return;
			}

			for (int i = 0; i < contents.length; i++) {
				if (contents[i] == null) {
					continue;
				}

				label = new Label(i, tempSize, contents[i]);
				sheet.addCell(label);
			}

			tempSize++;
			if (tempSize > maxCount) {
				tempSize = 0;
				sheet = null;
				sheetIndex++;
			}
		}
		return;
	}
}
