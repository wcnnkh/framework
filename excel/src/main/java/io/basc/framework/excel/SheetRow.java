package io.basc.framework.excel;

import io.basc.framework.mapper.io.table.Row;

public interface SheetRow extends Row {
	SheetContext getSheetContext();
}
