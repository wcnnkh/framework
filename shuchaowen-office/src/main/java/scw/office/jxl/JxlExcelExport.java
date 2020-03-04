package scw.office.jxl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import scw.core.utils.CollectionUtils;
import scw.office.excel.ExcelExport;

public class JxlExcelExport implements ExcelExport {
	// 创建Excel工作薄
	private int tempSize = 0;
	private int maxCount = 60000;// 一个sheet最多放多少数据
	private WritableSheet sheet;
	private int sheetIndex = 1;
	private Label label;
	private String[] title;
	private WritableWorkbook wwb;
	
	public JxlExcelExport(OutputStream os, String[] title) throws IOException{
		this(Workbook.createWorkbook(os), title);
	}

	public JxlExcelExport(WritableWorkbook wwb, String[] title) {
		this.wwb = wwb;
		this.title = title;
	}

	public void close() {
		try {
			wwb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected final void appendRowInternal(String[] row) throws IOException{
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
		
		if (row == null || row.length == 0) {
			return;
		}

		for (int i = 0; i < row.length; i++) {
			if (row[i] == null) {
				continue;
			}

			label = new Label(i, tempSize, row[i]);
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

	public void append(String[] row) throws IOException{
		appendRowInternal(row);
		wwb.write();
	}

	public void append(Collection<String[]> rows) throws IOException{
		if(CollectionUtils.isEmpty(rows)){
			return ;
		}
		
		for(String[] row : rows){
			appendRowInternal(row);
		}
		wwb.write();
	}
}
