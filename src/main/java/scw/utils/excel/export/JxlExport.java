package scw.utils.excel.export;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import scw.db.DB;
import scw.sql.RowCallback;
import scw.sql.Sql;

/**
 * 文件名中如果存在中文，标点符号应该使用全角的符号
 * 
 * @author shuchaowen
 *
 */
public class JxlExport {
	/**
	 * 导出excel
	 * 
	 * @param fileName
	 *            要生成的文件名
	 * @param title
	 *            列名
	 * @param tempList
	 *            数据源
	 * @param response
	 *            HttpServletResponse
	 * @throws Exception
	 */
	public static void exportExcel(String fileName, String title[], List<Object[]> tempList,
			HttpServletResponse response) throws Exception {
		String oldFileName = fileName;
		fileName = new String(fileName.getBytes(), "iso-8859-1");

		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");

		// 创建Excel工作薄
		OutputStream os = response.getOutputStream();
		WritableWorkbook wwb = Workbook.createWorkbook(os);
		// 添加第一个工作表并设置第一个Sheet的名字
		int size = tempList.size();// 数据量
		int maxCount = 60000;// 一个sheet最多放多少数据
		if (size > 0) {
			int count = (size / maxCount) + 1;
			for (int j = 1; j <= count; j++) {
				// 为了解决size是maxCount整数倍报错的情况
				if (size == (maxCount * (j - 1))) {
					continue;
				}

				WritableSheet sheet = wwb.createSheet(oldFileName.trim() + String.valueOf(j), j - 1);
				Label label;
				// Label(x,y,z) 代表单元格的第x+1列，第y+1行, 内容z
				for (int i = 0; i < title.length; i++) {
					// 在Label对象的子对象中指明单元格的位置和内容
					label = new Label(i, 0, title[i]);
					// 将定义好的单元格添加到工作表中
					sheet.addCell(label);
				}

				int formListIndex = (j - 1) * maxCount;
				int toRow = maxCount;
				if (count == j) {
					toRow = tempList.size() % maxCount;
				}

				// 开始写入
				for (int r = 1; r <= toRow; r++) {
					Object[] obj = tempList.get(formListIndex + r - 1);
					for (int c = 0; c < obj.length; c++) {
						if (obj != null) {
							String centent = String.valueOf(obj[c]);
							label = new Label(c, r, centent);
							sheet.addCell(label);
						}
					}
				}
			}
		}
		// 写入数据
		wwb.write();
		// 关闭文件
		wwb.close();
		response.flushBuffer();
	}

	public static void sqlResultSetToExcel(String fileName, String title[], DB db, HttpServletResponse response,
			SqlExportRow exportRow, Sql... sqls) throws Exception {
		fileName = new String(fileName.getBytes(), "iso-8859-1");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");

		// 创建Excel工作薄
		OutputStream os = response.getOutputStream();
		sqlResultSetToExcel(title, db, Arrays.asList(sqls), os, exportRow);
		response.flushBuffer();
	}

	public static void sqlResultSetToExcel(String fileName, String title[], DB db, List<Sql> sqlList,
			HttpServletResponse response, SqlExportRow exportRow) throws Exception {
		fileName = new String(fileName.getBytes(), "iso-8859-1");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");

		// 创建Excel工作薄
		OutputStream os = response.getOutputStream();
		sqlResultSetToExcel(title, db, sqlList, os, exportRow);
		response.flushBuffer();
	}

	public static void sqlResultSetToExcel(String title[], DB db, List<Sql> sqlList, OutputStream os,
			SqlExportRow exportRow) throws Exception {
		// 创建Excel工作薄
		WritableWorkbook wwb = Workbook.createWorkbook(os);
		final ResultSetToExeclRowCall rowCall = new ResultSetToExeclRowCall(wwb, title, exportRow);
		for (Sql sql : sqlList) {
			db.query(sql, new RowCallback() {

				public void processRow(ResultSet rs, int rowNum) throws SQLException {
					try {
						rowCall.format(rs);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		// 写入数据
		wwb.write();
		// 关闭文件
		wwb.close();
		os.flush();
	}
}
