package run.soeasy.framework.dom.convert;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;

@RequiredArgsConstructor
public class MapWriter extends AbstractNodeWriter {
	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return sourceDescriptor.getReturnTypeDescriptor().isMap();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void writeTo(TypedValue source, Node node) throws DOMException {
		for (Entry entry : (Set<Entry>) ((Map) source.get()).entrySet()) {
			Object key = entry.getKey();
			if (key == null) {
				continue;
			}

			String name = (String) getConversionService().convert(entry.getKey(),
					source.getReturnTypeDescriptor().getMapKeyTypeDescriptor(), TypeDescriptor.valueOf(String.class));
			TypedValue value = TypedValue.of(entry.getValue(), source.getReturnTypeDescriptor().getMapValueTypeDescriptor());
			if (getNodeWriter().isWriteable(value)) {
				Element element = node.getOwnerDocument().createElement(name);
				getNodeWriter().writeTo(value, element);
				node.appendChild(element);
			}
		}
	}
}
