package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Value;
import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Wrapper;
import lombok.NonNull;

/**
 * 模板
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Template<K, V extends Value> {
	public static interface TemplateWrapper<K, V extends Value, W extends Template<K, V>>
			extends Template<K, V>, Wrapper<W> {
		@Override
		default Elements<K> getAccessorIndexes() {
			return getSource().getAccessorIndexes();
		}

		@Override
		default Elements<V> getAccessors(@NonNull K index) {
			return getSource().getAccessors(index);
		}
	}

	Elements<K> getAccessorIndexes();

	Elements<V> getAccessors(@NonNull K index);
}
