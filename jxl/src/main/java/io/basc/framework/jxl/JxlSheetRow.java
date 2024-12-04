package io.basc.framework.jxl;

import io.basc.framework.core.convert.transform.Parameter;
import io.basc.framework.excel.SheetColumn;
import io.basc.framework.excel.SheetContext;
import io.basc.framework.excel.SheetRow;
import io.basc.framework.util.Elements;
import io.basc.framework.util.SimpleItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JxlSheetRow extends SimpleItem implements SheetRow {
	@NonNull
	private final SheetColumn[] columns;
	@NonNull
	private final SheetContext sheetContext;

	@Override
	public Elements<Parameter> getElements() {
		return Elements.forArray(columns);
	}
}
