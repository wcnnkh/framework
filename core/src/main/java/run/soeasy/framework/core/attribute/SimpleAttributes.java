package run.soeasy.framework.core.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;
import run.soeasy.framework.core.collection.Elements;

@Data
public class SimpleAttributes<K, V> implements EditableAttributes<K, V> {
	private Map<K, V> attributeMap;

	public SimpleAttributes() {
	}

	public SimpleAttributes(Map<K, V> attributeMap) {
		this.attributeMap = attributeMap;
	}

	public V getAttribute(K name) {
		return attributeMap == null ? null : attributeMap.get(name);
	}

	public Elements<K> getAttributeNames() {
		return attributeMap == null ? Elements.empty() : Elements.of(attributeMap.keySet());
	}

	public void setAttribute(K name, V o) {
		if (attributeMap == null) {
			attributeMap = new LinkedHashMap<>(8);
		}

		attributeMap.put(name, o);
	}

	public void removeAttribute(K name) {
		if (attributeMap == null) {
			return;
		}

		attributeMap.remove(name);
	}

	public void clearAttributes() {
		if (attributeMap != null) {
			attributeMap.clear();
		}
	}
}
