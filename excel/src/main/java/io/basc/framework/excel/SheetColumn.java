package io.basc.framework.excel;

import io.basc.framework.mapper.io.table.Column;

public interface SheetColumn extends Column {
	SheetContext getSheetContext();
}
