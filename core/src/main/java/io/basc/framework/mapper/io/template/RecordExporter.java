package io.basc.framework.mapper.io.template;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.io.Exporter;

public interface RecordExporter extends Exporter {
	RecordConverter getConverter();

	@Override
	default void doWrite(Object data, TypeDescriptor typeDescriptor) throws IOException {
		if (getConverter().canReverseConvert(typeDescriptor, Record.class)) {
			Record record = getConverter().reverseConvert(data, typeDescriptor, Record.class);
			doWriteRecord(record);
		}
	}

	void doWriteRecord(Record record) throws IOException;
}
