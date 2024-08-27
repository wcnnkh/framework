package io.basc.framework.mapper.io.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.io.Importer;
import io.basc.framework.util.Elements;

public interface RecordImporter extends Importer {
	RecordConverter getConverter();

	default Elements<Record> readAllRecords() throws IOException {
		List<Record> list = new ArrayList<>();
		doRead((data, type) -> {
			if (getConverter().canReverseConvert(type, Record.class)) {
				Record row = getConverter().reverseConvert(data, type, Record.class);
				list.add(row);
			}
		});
		return Elements.of(list);
	}

	default <T> Elements<T> readAllRecords(Class<T> requiredType) throws IOException {
		return readAllRecords(TypeDescriptor.valueOf(requiredType));
	}

	@SuppressWarnings("unchecked")
	default <T> Elements<T> readAllRecords(TypeDescriptor typeDescriptor) throws IOException {
		List<T> list = new ArrayList<>();
		doRead((data, type) -> {
			if (getConverter().canReverseConvert(type, typeDescriptor)) {
				T row = (T) getConverter().reverseConvert(data, type, typeDescriptor);
				list.add(row);
			}
		});
		return Elements.of(list);
	}
}
