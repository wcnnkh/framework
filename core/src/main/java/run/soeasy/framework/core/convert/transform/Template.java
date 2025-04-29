package run.soeasy.framework.core.convert.transform;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.alias.Named;
import run.soeasy.framework.core.collection.KeyValueListable;
import run.soeasy.framework.core.convert.TypedValueAccessor;

/**
 * 模板
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Template<K, V extends TypedValueAccessor> extends KeyValueListable<K, V, KeyValue<K, V>>, Named {
	public static interface TemplateWrapper<K, V extends TypedValueAccessor, W extends Template<K, V>>
			extends Template<K, V>, KeyValueListableWrapper<K, V, KeyValue<K, V>, W>, NamedWrapper<W> {

		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default Template<K, V> rename(String name) {
			return getSource().rename(name);
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class RenamedTemplate<K, V extends TypedValueAccessor, W extends Template<K, V>>
			implements TemplateWrapper<K, V, W> {
		@NonNull
		private final W source;
		private final String name;

		@Override
		public Template<K, V> rename(String name) {
			return new RenamedTemplate<>(source, name);
		}
	}

	@Override
	default String getName() {
		return null;
	}

	@Override
	default Template<K, V> rename(String name) {
		return new RenamedTemplate<>(this, name);
	}
}
