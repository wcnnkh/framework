package io.basc.framework.poi.hssf;

import org.apache.poi.hssf.record.BoundSheetRecord;

import io.basc.framework.excel.SheetContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoundSheetContext implements SheetContext {
	private final int positionIndex;
	private final BoundSheetRecord boundSheetRecord;

	@Override
	public String getName() {
		return boundSheetRecord == null ? null : boundSheetRecord.getSheetname();
	}
}
