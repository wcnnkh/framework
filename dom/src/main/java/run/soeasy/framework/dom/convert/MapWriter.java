package run.soeasy.framework.dom.convert;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.TypeDescriptor;

@RequiredArgsConstructor
public class MapWriter extends AbstractNodeWriter {
	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return sourceDescriptor.getTypeDescriptor().isMap();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void writeTo(Source source, Node node) throws DOMException {
		for (Entry entry : (Set<Entry>) ((Map) source.get()).entrySet()) {
			Object key = entry.getKey();
			if (key == null) {
				continue;
			}

			String name = (String) getConversionService().convert(entry.getKey(),
					source.getTypeDescriptor().getMapKeyTypeDescriptor(), TypeDescriptor.valueOf(String.class));
			Source value = Source.of(entry.getValue(), source.getTypeDescriptor().getMapValueTypeDescriptor());
			if (getNodeWriter().isWriteable(value)) {
				Element element = node.getOwnerDocument().createElement(name);
				getNodeWriter().writeTo(value, element);
				node.appendChild(element);
			}
		}
	}
}
