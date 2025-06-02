package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.transform.lang.MappingService;

@Getter
public class TemplateMappingService<E extends TypedValueAccessor>
		extends MappingService<Object, E, TemplateMapping<E>> {
}
