package run.soeasy.framework.core.transform.indexed;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Lookup;
import run.soeasy.framework.core.transform.Mapping;

public interface IndexedMapping<T extends IndexedAccessor>
		extends Mapping<Object, T>, Lookup<Object, T>, IndexedTemplate<T> {

	public static interface IndexedMappingWrapper<T extends IndexedAccessor, W extends IndexedMapping<T>> extends
			IndexedMapping<T>, MappingWrapper<Object, T, W>, LookupWrapper<Object, T, W>, IndexedTemplateWrapper<T, W> {

		@Override
		default T get(Object key) {
			return getSource().get(key);
		}

		@Override
		default IndexedMapping<T> randomAccess() {
			return getSource().randomAccess();
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
	default IndexedMapping<T> randomAccess() {
		return new RandomAccessIndexedMapping<>(this);
	}
}
