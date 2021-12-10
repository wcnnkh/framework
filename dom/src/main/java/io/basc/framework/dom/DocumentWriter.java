package io.basc.framework.dom;

import io.basc.framework.convert.TypeDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface DocumentWriter {
	boolean canWrite(TypeDescriptor typeDescriptor);

	void write(Document document, Node parentNode, String nodeName, Object value, TypeDescriptor valueType);
}
