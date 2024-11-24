package io.basc.framework.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import io.basc.framework.core.convert.TypeDescriptor;

public interface DocumentWriter {
	boolean canWrite(TypeDescriptor typeDescriptor);

	void write(Document document, Node parentNode, String nodeName, Object value, TypeDescriptor valueType);
}
