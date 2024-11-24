package io.basc.framework.mapper.io.table;

import io.basc.framework.core.execution.param.Parameter;
import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.util.Elements;
import io.basc.framework.util.SimpleItem;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RecordRow extends SimpleItem implements Row {
	private final Record record;

	@Override
	public Elements<Parameter> getElements() {
		return record.getElements();
	}

	public Record getRecord() {
		return record;
	}
}
