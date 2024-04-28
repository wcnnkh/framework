package io.basc.framework.excel;

import io.basc.framework.value.Value;

public interface Record extends Value {
	int getRowIndex();

	int getColumnIndex();
}
