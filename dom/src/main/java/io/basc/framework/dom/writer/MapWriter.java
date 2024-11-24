package io.basc.framework.dom.writer;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.dom.DocumentWriter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MapWriter implements DocumentWriter {
	private final DocumentWriter writer;

	public MapWriter(DocumentWriter writer) {
		this.writer = writer;
	}

	@Override
	public boolean canWrite(TypeDescriptor sourceTypeDescriptor) {
		return sourceTypeDescriptor.isMap();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void write(Document document, Node parentNode, String nodeName, Object source,
			TypeDescriptor sourceTypeDescriptor) {
		Element element = document.createElement(String.valueOf(nodeName));
		for (Entry entry : (Set<Entry>) ((Map) source).entrySet()) {
			Object key = entry.getKey();
			if (key == null) {
				continue;
			}
			writer.write(document, parentNode, String.valueOf(key), entry.getValue(),
					sourceTypeDescriptor.getMapValueTypeDescriptor(entry.getValue()));
		}
		parentNode.appendChild(element);
	}

}
