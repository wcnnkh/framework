package io.basc.framework.util.attribute;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;

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
