package io.basc.framework.util.attribute;

import io.basc.framework.util.Wrapper;
import io.basc.framework.util.element.Elements;

public class AttributesWrapper<W extends Attributes<K, V>, K, V> extends Wrapper<W> implements Attributes<K, V> {

	public AttributesWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public V getAttribute(K name) {
		return wrappedTarget.getAttribute(name);
	}

	@Override
	public Elements<K> getAttributeNames() {
		return wrappedTarget.getAttributeNames();
	}

}
