package scw.dom.append;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import scw.convert.TypeDescriptor;

public interface AppendChildService {
	boolean matches(TypeDescriptor sourceTypeDescriptor);
	
	void append(Document document, Node parentNode, String nodeName, Object source, TypeDescriptor sourceTypeDescriptor);
}
