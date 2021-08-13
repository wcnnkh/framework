package scw.orm.annotation;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.AnnotationAttributes;
import scw.core.annotation.Named;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.orm.PropertyDescriptor;

public class FieldPropertyDescriptor implements PropertyDescriptor {
	protected final Field field;

	public FieldPropertyDescriptor(Field field) {
		this.field = field;
	}

	@Override
	public String getName() {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(field,
				Named.class);
		if (annotationAttributes == null) {
			return field.getGetter().getName();
		}

		String name = annotationAttributes.getString("value");
		return StringUtils.isEmpty(name) ? field.getGetter().getName() : name;
	}

	@Override
	public boolean isPrimaryKey() {
		return AnnotatedElementUtils.isAnnotated(field, PrimaryKey.class);
	}
}
