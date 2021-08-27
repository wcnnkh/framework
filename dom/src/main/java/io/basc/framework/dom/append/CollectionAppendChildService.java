package io.basc.framework.dom.append;

import io.basc.framework.convert.TypeDescriptor;

import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CollectionAppendChildService implements AppendChildService{
	private final AppendChildService appendChild;
	
	public CollectionAppendChildService(AppendChildService appendChild){
		this.appendChild = appendChild;
	}
	
	public boolean matches(TypeDescriptor valueTypeDescriptor) {
		return valueTypeDescriptor.isCollection();
	}

	@SuppressWarnings("rawtypes")
	public void append(Document document, Node parentNode, String name, Object value,
			TypeDescriptor valueTypeDescriptor) {
		for(Object item : (Collection)value){
			appendChild.append(document, parentNode, name, value, valueTypeDescriptor.elementTypeDescriptor(item));
		}
	}
}
