package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Value;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultTemplateReader<K, SV extends Value, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends FilterableTemplateReader<K, SV, S, TV, T, E> {

	public DefaultTemplateReader() {
		super(new TemplateReadFilters<>());
	}

	@SuppressWarnings("unchecked")
	@Override
	public TemplateReadFilters<K, SV, S, TV, T, E> getTemplateReadFilters() {
		return (TemplateReadFilters<K, SV, S, TV, T, E>) super.getTemplateReadFilters();
	}

}
