package run.soeasy.framework.core.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.function.Wrapper;

/**
 * 模板
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Template<K, V extends Source> {
	public static interface TemplateWrapper<K, V extends Source, W extends Template<K, V>>
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
