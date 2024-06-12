package io.basc.framework.poi;

import org.apache.poi.ss.usermodel.Cell;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.value.AbstractValue;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PoiCell extends AbstractValue implements io.basc.framework.excel.Cell {
	private static Logger logger = LoggerFactory.getLogger(PoiCell.class);

	@NonNull
	private final Cell cell;

	@Override
	public Object getSource() {
		return cell;
	}

	@Override
	protected Object unwrapSource(Object source, TypeDescriptor sourceTypeDescriptor) {
		if (source instanceof Cell) {
			Cell cell = (Cell) source;
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
		return source;
	}

	@Override
	public int getRowIndex() {
		return cell.getRowIndex();
	}

	@Override
	public int getColumnIndex() {
		return cell.getColumnIndex();
	}

}
