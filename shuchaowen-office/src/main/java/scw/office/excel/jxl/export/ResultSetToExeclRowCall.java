package scw.office.excel.jxl.export;

import java.sql.ResultSet;
import java.sql.SQLException;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import scw.sql.ResultSetCallback;
import scw.sql.orm.ResultMapping;
import scw.sql.orm.support.DefaultResultMapping;

public class ResultSetToExeclRowCall implements ResultSetCallback{
	// 创建Excel工作薄
	private int tempSize = 0;
	private int maxCount = 60000;// 一个sheet最多放多少数据
	private WritableSheet sheet;
	private int sheetIndex = 1;
	private Label label;
	private String[] title;
	private WritableWorkbook wwb;
	private SqlExportRow exportRow;

	public ResultSetToExeclRowCall(WritableWorkbook wwb, String[] title, SqlExportRow exportRow) {
		this.wwb = wwb;
		this.title = title;
		this.exportRow = exportRow;
	}

	public void process(ResultSet resultSet) throws SQLException {
		if (sheet == null) {
			sheet = wwb.createSheet("sheet" + sheetIndex, sheetIndex - 1);
		}

		if (tempSize == 0) {
			// Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
			for (int i = 0; i < title.length; i++) {
				// 在Label对象的子对象中指明单元格的位置和内容
				label = new Label(i, 0, title[i]);
				// 将定义好的单元格添加到工作表中
				try {
					sheet.addCell(label);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}
			tempSize++;
		}

		while (resultSet.next()) {
			ResultMapping resultMapping = new DefaultResultMapping(resultSet);
			String[] contents = exportRow.exportRow(resultMapping);
			if (contents == null || contents.length == 0) {
				return;
			}

			for (int i = 0; i < contents.length; i++) {
				if (contents[i] == null) {
					continue;
				}

				label = new Label(i, tempSize, contents[i]);
				try {
					sheet.addCell(label);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
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
