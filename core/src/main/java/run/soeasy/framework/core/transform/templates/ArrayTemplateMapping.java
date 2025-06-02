package run.soeasy.framework.core.transform.templates;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

public class ArrayTemplateMapping<E extends TypedValueAccessor, W extends TemplateMapping<E>>
		extends ArrayTemplate<E, W> implements TemplateMappingWrapper<E, W> {

	public ArrayTemplateMapping(@NonNull W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public TemplateMapping<E> asArray(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
	}
}
