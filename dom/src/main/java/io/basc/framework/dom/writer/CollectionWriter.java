package io.basc.framework.dom.writer;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.DocumentWriter;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CollectionWriter implements DocumentWriter {
	private final DocumentWriter writer;

	public CollectionWriter(DocumentWriter writer) {
		this.writer = writer;
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor) {
		return typeDescriptor.isCollection();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void write(Document document, Node parentNode, String nodeName,
			Object value, TypeDescriptor valueType) {
		for (Object item : (Collection) value) {
			writer.write(document, parentNode, nodeName, value,
					valueType.elementTypeDescriptor(item));
		}
	}
}
