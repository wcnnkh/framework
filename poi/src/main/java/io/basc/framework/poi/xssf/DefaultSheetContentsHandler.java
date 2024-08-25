package io.basc.framework.poi.xssf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import io.basc.framework.excel.StandardSheetContext;
import io.basc.framework.mapper.io.Exporter;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSheetContentsHandler implements SheetContentsHandler {
	private static Logger logger = LoggerFactory.getLogger(DefaultSheetContentsHandler.class);
	@NonNull
	private final Exporter exporter;
	private List<String> contents = new ArrayList<String>();
	private int sheetIndex = 0;
	private String sheetName;

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public void startRow(int rowNum) {
	}

	public void endRow(int rowNum) {
		StandardSheetContext sheetContext = new StandardSheetContext();
		sheetContext.setPositionIndex(sheetIndex);
		sheetContext.setName(sheetName);

		XssfRow row = new XssfRow(sheetContext, contents);
		contents.clear();
		try {
			exporter.doWrite(row);
		} catch (IOException e) {
			logger.error(e, "do write row {}", row);
		}
	}

	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
		contents.add(formattedValue);
	}
}
