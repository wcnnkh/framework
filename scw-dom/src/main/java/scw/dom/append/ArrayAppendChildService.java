package scw.dom.append;

import java.lang.reflect.Array;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import scw.convert.TypeDescriptor;

public class ArrayAppendChildService implements AppendChildService{
	private final AppendChildService service;
	
	public ArrayAppendChildService(AppendChildService service){
		this.service = service;
	}
	
	public boolean matches(TypeDescriptor valueTypeDescriptor) {
		return valueTypeDescriptor.isArray();
	}

	public void append(Document document, Node parentNode, String name, Object value,
			TypeDescriptor valueTypeDescriptor) {
		for (int i = 0, len = Array.getLength(value); i < len; i++) {
			Object item = Array.get(value, i);
			service.append(document, parentNode, name, item, valueTypeDescriptor.elementTypeDescriptor(item));
		}
	}

}
