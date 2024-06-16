package io.basc.framework.excel;

import io.basc.framework.mapper.io.table.Column;
import io.basc.framework.mapper.io.table.SimpleRow;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleSheetRow extends SimpleRow implements SheetRow, SheetContextAware {
	private SheetContext sheetContext;

	public SimpleSheetRow(Column[] columns) {
		super(columns);
	}
}
