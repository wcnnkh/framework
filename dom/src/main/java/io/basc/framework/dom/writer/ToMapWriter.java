package io.basc.framework.dom.writer;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.DocumentWriter;
import io.basc.framework.mapper.ToMap;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ToMapWriter implements DocumentWriter {
	private final DocumentWriter writer;

	public ToMapWriter(DocumentWriter writer) {
		this.writer = writer;
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor) {
		return ToMap.class.isAssignableFrom(typeDescriptor.getType());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void write(Document document, Node parentNode, String nodeName,
			Object value, TypeDescriptor valueType) {
		Map map = ((ToMap) value).toMap();
		if (map == null) {
			return;
		}

		writer.write(document, parentNode, nodeName, map, valueType.narrow(map));
	}
}
