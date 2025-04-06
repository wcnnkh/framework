package run.soeasy.framework.core.transform.stereotype;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;

@Getter
@Setter
public class DefaultTemplateReader<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
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
