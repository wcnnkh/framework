package run.soeasy.framework.core.convert.property;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Lookup;
import run.soeasy.framework.core.convert.TypedValueAccessor;
import run.soeasy.framework.core.convert.transform.Mapping;

public interface Dictionary<T extends TypedValueAccessor> extends Mapping<Object, T>, Lookup<Object, T> {

	public static interface DictionaryWrapper<T extends TypedValueAccessor, W extends Dictionary<T>>
			extends Dictionary<T>, MappingWrapper<Object, T, W>, LookupWrapper<Object, T, W> {

		@Override
		default T get(Object key) {
			return getSource().get(key);
		}

		@Override
		default Dictionary<T> rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default int size() {
			return getSource().size();
		}
	}

	public static class RenamedDictionary<T extends TypedValueAccessor, W extends Dictionary<T>>
			extends RenamedMapping<Object, T, W> implements DictionaryWrapper<T, W> {

		public RenamedDictionary(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public Dictionary<T> rename(String name) {
			return new RenamedDictionary<>(getSource(), name);
		}
	}

	@Override
	default T get(Object key) {
		Elements<T> values = getValues(key);
		if (values == null) {
			return null;
		}

		return values.isUnique() ? null : values.getUnique();
	}

	@Override
	default Dictionary<T> rename(String name) {
		return new RenamedDictionary<>(this, name);
	}

	default int size() {
		return getElements().count().intValue();
	}
}
