package io.basc.framework.mapper.transfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.util.element.Elements;

public interface RecordImporter extends Importer {
	ObjectMapper getMapper();

	default <T> Elements<T> doReadAll(Class<? extends T> type) throws IOException {
		return doReadAll(TypeDescriptor.valueOf(type));
	}

	@SuppressWarnings("unchecked")
	default <T> Elements<T> doReadAll(TypeDescriptor typeDescriptor) throws IOException {
		List<T> list = new ArrayList<>();
		DefaultRecordExporter defaultRecordExporter = new DefaultRecordExporter(typeDescriptor, (e, type) -> {
			T element = (T) e;
			list.add(element);
		});
		defaultRecordExporter.setMapper(getMapper());
		doRead(defaultRecordExporter);
		return Elements.of(list);
	}
}
