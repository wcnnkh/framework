package io.basc.framework.dom.append;

import io.basc.framework.convert.TypeDescriptor;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface AppendChildService {
	boolean matches(TypeDescriptor sourceTypeDescriptor);
	
	void append(Document document, Node parentNode, String nodeName, Object source, TypeDescriptor sourceTypeDescriptor);
}
