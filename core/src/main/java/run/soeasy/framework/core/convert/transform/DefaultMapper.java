package run.soeasy.framework.core.convert.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypedValueAccessor;

public class DefaultMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends FilterableMapper<K, V, T>
		implements Mapper<K, V, T> {

	public DefaultMapper() {
		super(new MappingFilters<>(), new GenericMapper<>());
	}

	@Override
	public @NonNull MappingFilters<K, V, T> getFilters() {
		return (MappingFilters<K, V, T>) super.getFilters();
	}

	@Override
	public @NonNull GenericMapper<K, V, T> getMapper() {
		return (GenericMapper<K, V, T>) super.getMapper();
	}
}
