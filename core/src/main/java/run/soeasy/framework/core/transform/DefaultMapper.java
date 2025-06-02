package run.soeasy.framework.core.transform;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class DefaultMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends GenericMapper<K, V, T>
		implements Mapper<K, V, T> {

	public DefaultMapper() {
		super(new ConfigurableServices<>(), new ValueMapper<>());
	}

	@Override
	public @NonNull ConfigurableServices<MappingFilter<K, V, T>> getFilters() {
		return (ConfigurableServices<MappingFilter<K, V, T>>) super.getFilters();
	}

	@Override
	public @NonNull ValueMapper<K, V, T> getMapper() {
		return (ValueMapper<K, V, T>) super.getMapper();
	}
}
