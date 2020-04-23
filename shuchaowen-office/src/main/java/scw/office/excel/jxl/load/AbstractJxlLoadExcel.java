package scw.office.excel.jxl.load;

import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import scw.io.resource.ResourceUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.queue.Consumer;

public abstract class AbstractJxlLoadExcel<T> extends AbstractLoadRow<T>
		implements Runnable, LoadRow {
	private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private final String excel;

	public AbstractJxlLoadExcel(String excel, Class<T> type,
			int nameMappingIndex, int beginRowIndex, int endRowIndex) {
		super(type, nameMappingIndex, beginRowIndex, endRowIndex);
		this.excel = excel;
	}

	public void run() {
		logger.debug("开始读取:{}", excel);
		ResourceUtils.getResourceOperations().consumterInputStream(excel, new Consumer<InputStream>() {

			public void consume(InputStream message) throws Exception {
				final long t = System.currentTimeMillis();
				Workbook workbook = null;
				try {
					workbook = Workbook.getWorkbook(message);
					Sheet[] sheets = workbook.getSheets();
					for (int sheetIndex = 0; sheetIndex < sheets.length; sheetIndex++) {
						Sheet sheet = sheets[sheetIndex];
						for (int rowIndex = 0; rowIndex < sheet.getRows(); rowIndex++) {
							int columns = sheet.getColumns();
							String[] contents = new String[columns];
							for (int columnIndex = 0; columnIndex < columns; columnIndex++) {
								Cell cell = sheet
										.getCell(columnIndex, rowIndex);
								String content = cell.getContents();
								if (content != null) {
									content = content.trim();
								}
								contents[columnIndex] = content;
							}

							load(sheetIndex, rowIndex, contents);
						}
					}

					logger.debug("加载{}完成, 用时：{}ms", excel,
							(System.currentTimeMillis() - t));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (workbook != null) {
						workbook.close();
					}
				}
			}
		});
	}

}
