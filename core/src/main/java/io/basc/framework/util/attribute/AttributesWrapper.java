package io.basc.framework.util.attribute;

import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.function.Wrapper;

public interface AttributesWrapper<K, V, W extends Attributes<K, V>> extends Attributes<K, V>, Wrapper<W> {

	@Override
	default V getAttribute(K name) {
		return getSource().getAttribute(name);
	}

	@Override
	default Elements<K> getAttributeNames() {
		return getSource().getAttributeNames();
	}
}
