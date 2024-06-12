package io.basc.framework.jxl;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.value.AbstractValue;
import io.basc.framework.excel.SheetContext;
import jxl.Cell;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JxlCell extends AbstractValue implements io.basc.framework.excel.Cell {
	private final Cell cell;
	private final SheetContext sheetContext;

	@Override
	public Object getSource() {
		return cell;
	}

	@Override
	protected Object unwrapSource(Object source, TypeDescriptor sourceTypeDescriptor) {
		if (source instanceof Cell) {
			return ((Cell) source).getContents();
		}
		return source;
	}

	@Override
	public int getRowIndex() {
		return cell.getRow();
	}

	@Override
	public int getColumnIndex() {
		return cell.getColumn();
	}

	@Override
	public SheetContext getSheetContext() {
		return sheetContext;
	}
}
