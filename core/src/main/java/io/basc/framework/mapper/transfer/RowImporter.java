package io.basc.framework.mapper.transfer;

import io.basc.framework.util.element.Elements;
import io.basc.framework.value.Value;

public interface RowImporter extends RecordImporter {
	SimpleTitles getTitles();

	default Elements<Value[]> doReadAll() {
		// TODO
		return null;
	}
}
