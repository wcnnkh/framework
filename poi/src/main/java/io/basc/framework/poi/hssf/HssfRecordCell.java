package io.basc.framework.poi.hssf;

import org.apache.poi.hssf.record.Record;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.excel.Cell;
import io.basc.framework.excel.SheetContext;
import io.basc.framework.lang.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HssfRecordCell implements Cell {
	@Nullable
	private final Record record;

	@Override
	public int getRowIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getColumnIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Value getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SheetContext getSheetContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
