package run.soeasy.framework.core.attribute;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;

public interface Attributes<K, V> {
	public static interface AttributesWrapper<K, V, W extends Attributes<K, V>> extends Attributes<K, V>, Wrapper<W> {
		@Override
		default V getAttribute(K name) {
			return getSource().getAttribute(name);
		}

		@Override
		default Elements<K> getAttributeNames() {
			return getSource().getAttributeNames();
		}
	}

	V getAttribute(K name);

	Elements<K> getAttributeNames();
}
