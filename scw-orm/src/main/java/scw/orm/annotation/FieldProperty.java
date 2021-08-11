package scw.orm.annotation;

import java.lang.reflect.Field;

import scw.core.annotation.AnnotatedElementUtils;
import scw.core.annotation.AnnotationAttributes;
import scw.core.annotation.Named;
import scw.core.utils.StringUtils;
import scw.orm.SharedProperty;

public class FieldProperty extends SharedProperty{
	private static final long serialVersionUID = 1L;

	public FieldProperty(Field field){
		setName(getName(field));
		setPrimaryKey(AnnotatedElementUtils.isAnnotated(field, PrimaryKey.class));
	}
	
	protected String getName(Field field) {
		AnnotationAttributes annotationAttributes = AnnotatedElementUtils
				.getMergedAnnotationAttributes(field, Named.class);
		if (annotationAttributes == null) {
			return field.getName();
		}

		String name = annotationAttributes.getString("value");
		return StringUtils.isEmpty(name) ? field.getName() : name;
	}
}
