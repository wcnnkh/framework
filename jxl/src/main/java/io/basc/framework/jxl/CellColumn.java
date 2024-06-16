package io.basc.framework.jxl;

import io.basc.framework.excel.SheetColumn;
import io.basc.framework.excel.SheetContext;
import io.basc.framework.util.Item;
import jxl.Cell;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CellColumn implements SheetColumn {
	private final Cell cell;
	private final Item row;
	private final SheetContext sheetContext;

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getPositionIndex() {
		return cell.getColumn();
	}

	@Override
	public Item getRow() {
		return row;
	}

	@Override
	public Object getValue() {
		return cell.getContents();
	}
}
