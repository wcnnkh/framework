package scw.orm.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import scw.core.reflect.AnnotationFactory;
import scw.core.reflect.SimpleAnnotationFactory;
import scw.core.utils.StringUtils;

public class DefaultColumn extends FieldColumn {
	private String name;
	private AnnotationFactory annotationFactory;

	public DefaultColumn(Class<?> clazz, Field field) {
		super(clazz, field);
		this.annotationFactory = new SimpleAnnotationFactory(field);
		scw.orm.annotation.ColumnName columnName = getAnnotation(scw.orm.annotation.ColumnName.class);
		if (columnName != null && !StringUtils.isEmpty(columnName.value())) {
			this.name = columnName.value();
		}
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return annotationFactory.getAnnotation(type);
	}

	public String getName() {
		return name == null ? super.getName() : name;
	}
}
