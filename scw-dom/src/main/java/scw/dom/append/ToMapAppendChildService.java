package scw.dom.append;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import scw.convert.TypeDescriptor;
import scw.mapper.ToMap;

public class ToMapAppendChildService implements AppendChildService{
	private final AppendChildService appendChild;
	
	public ToMapAppendChildService(AppendChildService appendChild){
		this.appendChild = appendChild;
	}

	public boolean matches(TypeDescriptor valueTypeDescriptor) {
		return ToMap.class.isAssignableFrom(valueTypeDescriptor.getType());
	}

	@SuppressWarnings("rawtypes")
	public void append(Document document, Node parentNode, String name, Object value,
			TypeDescriptor valueTypeDescriptor) {
		Map map = ((ToMap)value).toMap();
		appendChild.append(document, parentNode, name, map, valueTypeDescriptor.narrow(map));
	}

}
