package io.basc.framework.poi.ss;

import org.apache.poi.ss.usermodel.Cell;

import io.basc.framework.excel.SheetColumn;
import io.basc.framework.excel.SheetContext;
import io.basc.framework.util.Item;
import io.basc.framework.util.SimpleItem;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CellColumn implements SheetColumn {
	private static Logger logger = LogManager.getLogger(CellColumn.class);
	private final Cell cell;
	private final Item row;
	private final SheetContext sheetContext;

	public CellColumn(Cell cell, SheetContext sheetContext) {
		this(cell, new SimpleItem(cell.getRowIndex()), sheetContext);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getPositionIndex() {
		return cell.getColumnIndex();
	}

	@Override
	public Object getValue() {
		switch (cell.getCellType()) {
		case BLANK:
			return "";
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case NUMERIC:
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue();
		default:
			logger.warn("Unable to read this cell rowIndex[{}] colIndex[{}] cellType[{}]", cell.getRowIndex(),
					cell.getColumnIndex(), cell.getCellType());
			return null;
		}
	}

}
