package scw.dom.append;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import scw.convert.TypeDescriptor;

public class LastAppendChildService implements AppendChildService{
	
	public boolean matches(TypeDescriptor valueTypeDescriptor) {
		return true;
	}

	public void append(Document document, Node parentNode, String name, Object value,
			TypeDescriptor valueTypeDescriptor) {
		if(name == null || value == null){
			return ;
		}
		
		Element element = document.createElement(name);
		element.setTextContent(String.valueOf(value));
		parentNode.appendChild(element);
	}
}
