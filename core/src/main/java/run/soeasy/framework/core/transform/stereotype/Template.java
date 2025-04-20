package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Listable;

/**
 * 模板
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Template<K, V extends Accessor> extends Listable<KeyValue<K, V>> {
	public static interface TemplateWrapper<K, V extends Accessor, W extends Template<K, V>>
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
