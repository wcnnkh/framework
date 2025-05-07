package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.alias.Named;
import run.soeasy.framework.core.collection.KeyValueListable;
import run.soeasy.framework.core.convert.TypedValueAccessor;

/**
 * 映射
 * 
 * @author wcnnkh
 *
 * @param <K>
 * @param <V>
 */
public interface Mapping<K, V extends TypedValueAccessor> extends KeyValueListable<K, V, KeyValue<K, V>>, Named {
	public static interface MappingWrapper<K, V extends TypedValueAccessor, W extends Mapping<K, V>>
			extends Mapping<K, V>, KeyValueListableWrapper<K, V, KeyValue<K, V>, W>, NamedWrapper<W> {

		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default Mapping<K, V> rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedMapping<K, V extends TypedValueAccessor, W extends Mapping<K, V>> extends Renamed<W>
			implements MappingWrapper<K, V, W> {

		public RenamedMapping(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public Mapping<K, V> rename(String name) {
			return new RenamedMapping<>(getSource(), name);
		}
	}

	@Override
	default String getName() {
		return null;
	}

	@Override
	default Mapping<K, V> rename(String name) {
		return new RenamedMapping<>(this, name);
	}
}
