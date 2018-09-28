package shuchaowen.web.support.jxl.export;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import shuchaowen.core.db.result.ResultSet;
import shuchaowen.web.support.jxl.export.service.impl.SqlExportRowImpl;

public class ResultSetToExeclRowCall{
	// 创建Excel工作薄
	private int tempSize = 0;
	private int maxCount = 60000;// 一个sheet最多放多少数据
	private WritableSheet sheet;
	private int sheetIndex = 1;
	private Label label;
	private String[] title;
	private WritableWorkbook wwb;
	private SqlExportRowImpl exportRow;
	
	public ResultSetToExeclRowCall(WritableWorkbook wwb, String[] title, SqlExportRowImpl exportRow) {
			this.wwb = wwb;
			this.title = title;
			this.exportRow = exportRow;
	}

	public void format(ResultSet resultSet) throws Exception {
		if(resultSet == null || resultSet.getDataList() == null || resultSet.getDataList().isEmpty()){
			return ;
		}
		
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
		
		for(int index=0; index<resultSet.getDataList().size(); index++){
			String[] contents = exportRow.exportRow(resultSet.get(index));
			if(contents == null || contents.length == 0){
				return ;
			}
			
			for(int i=0; i<contents.length; i++){
				if(contents[i] == null){
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
		return ;
	}
}
