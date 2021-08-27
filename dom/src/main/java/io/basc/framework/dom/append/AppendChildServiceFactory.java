package io.basc.framework.dom.append;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.Synchronized;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class AppendChildServiceFactory implements AppendChildService, Comparator<AppendChildService> {
	protected final TreeSet<AppendChildService> services = new TreeSet<AppendChildService>(
			this);
	
	public SortedSet<AppendChildService> getServices() {
		return Synchronized.proxy(services, this);
	}

	public int compare(AppendChildService o1, AppendChildService o2) {
		return -1;
	}

	public AppendChildService getAppendChildService(TypeDescriptor valueTypeDescriptor) {
		for (AppendChildService appendChild : services) {
			if (appendChild.matches(valueTypeDescriptor)) {
				return appendChild;
			}
		}
		return null;
	}

	public boolean matches(TypeDescriptor valueTypeDescriptor) {
		return getAppendChildService(valueTypeDescriptor) != null;
	}

	public void append(Document document, Node parentNode, String name, Object value,
			TypeDescriptor valueTypeDescriptor) {
		AppendChildService service = getAppendChildService(valueTypeDescriptor);
		if (service == null) {
			throw new NotSupportedException(valueTypeDescriptor.toString());
		}
		service.append(document, parentNode, name, value, valueTypeDescriptor);
	}
	
	public Element createElement(Document document, String name, Object value){
		Element element = document.createElement(name);
		append(document, element, name, value, TypeDescriptor.forObject(value));
		return element;
	}
}
