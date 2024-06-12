package io.basc.framework.mapper.transfer;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.util.element.Elements;

public interface RowImporter extends RecordImporter {
	SimpleTitles getTitles();

	default Elements<Value[]> doReadAll() {
		// TODO
		return null;
	}
}
