package io.basc.framework.poi.hssf;

import org.apache.poi.hssf.record.BoundSheetRecord;

import io.basc.framework.excel.StandardCell;
import io.basc.framework.value.Value;
import lombok.Getter;

@Getter
public class HSSFCell extends StandardCell {

	public HSSFCell(int positionIndex, BoundSheetRecord boundSheetRecord, int rowIndex, int columnIndex, Value value) {
		super(new BoundSheetContext(positionIndex, boundSheetRecord), rowIndex, columnIndex, value);
	}
}
