package shuchaowen.web.util.excel.load;

import java.io.File;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import shuchaowen.core.util.Logger;

public class JxlLoadExcel extends LoadExcel{
	public JxlLoadExcel(String excel, LoadRow loadRow) {
		super(excel, loadRow);
	}
	
	public JxlLoadExcel(File excel, LoadRow loadRow) {
		super(excel, loadRow);
	}

	@Override
	public void load(File excel, LoadRow loadRow) throws Exception {
		Logger.info("开始读取" + excel.getName());
		long t = System.currentTimeMillis();
		Workbook workbook = Workbook.getWorkbook(excel);
		try {
			Sheet[] sheets = workbook.getSheets();
			for(int sheetIndex = 0; sheetIndex < sheets.length; sheetIndex++){
				Sheet sheet = sheets[sheetIndex];
				int rows = sheet.getRows();
				for(int rowIndex = 0; rowIndex < rows; rowIndex ++){
					int columns = sheet.getColumns();
					String[] contents = new String[columns];
					for(int columnIndex = 0; columnIndex < columns; columnIndex ++){
						Cell cell = sheet.getCell(columnIndex, rowIndex);
						String content = cell.getContents();
						if(content != null){
							content = content.trim();
						}
						contents[columnIndex] = content;
						loadRow.load(sheetIndex, rowIndex, contents);
					}
				}
			}
			Logger.info("加载" + excel.getName() + "完成, 用时：" + (System.currentTimeMillis() - t) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			workbook.close();
		}
	}
}
