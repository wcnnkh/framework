package io.basc.framework.excel;

import io.basc.framework.convert.lang.Value;

public interface Record extends Value {
	int getRowIndex();

	int getColumnIndex();
}
