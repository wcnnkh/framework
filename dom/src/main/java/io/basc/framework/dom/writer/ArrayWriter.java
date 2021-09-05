package io.basc.framework.dom.writer;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.DocumentWriter;

import java.lang.reflect.Array;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ArrayWriter implements DocumentWriter{
	private final DocumentWriter writer;
	
	public ArrayWriter(DocumentWriter writer) {
		this.writer = writer;
	}

	@Override
	public boolean canWrite(TypeDescriptor typeDescriptor) {
		return typeDescriptor.isArray();
	}

	@Override
	public void write(Document document, Node parentNode, String nodeName,
			Object value, TypeDescriptor valueType) {
		for (int i = 0, len = Array.getLength(value); i < len; i++) {
			Object item = Array.get(value, i);
			writer.write(document, parentNode, nodeName, item, valueType.elementTypeDescriptor(item));
		}
	}

}
