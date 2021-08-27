package io.basc.framework.dom.append;

import io.basc.framework.convert.TypeDescriptor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MapAppendChildService implements AppendChildService {
	private AppendChildService appendChild;
	
	public MapAppendChildService(AppendChildService appendChild){
		this.appendChild = appendChild;
	}

	public boolean matches(TypeDescriptor valueTypeDescriptor) {
		return valueTypeDescriptor.isMap();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void append(Document document, Node parentNode, String name, Object value,
			TypeDescriptor valueTypeDescriptor) {
		Element element = document.createElement(String.valueOf(name));
		for (Entry entry : (Set<Entry>) ((Map) value).entrySet()) {
			Object key = entry.getKey();
			if(key == null){
				continue;
			}
			appendChild.append(document, element, String.valueOf(key), entry.getValue(), valueTypeDescriptor.getMapValueTypeDescriptor(entry.getValue()));
		}
		parentNode.appendChild(element);
	};

}
