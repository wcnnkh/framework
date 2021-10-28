package io.basc.framework.orm.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFactory;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.orm.ObjectRelationalMapping;
import io.basc.framework.orm.annotation.AnnotationObjectRelationalResolverExtend;

public class DefaultObjectRelationalMapping extends DefaultObjectRelationalResolver implements ObjectRelationalMapping {
	private FieldFactory fieldFactory;

	public DefaultObjectRelationalMapping() {
		addService(new AnnotationObjectRelationalResolverExtend());
	}

	public FieldFactory getFieldFactory() {
		return fieldFactory == null ? MapperUtils.getFieldFactory() : fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	@Override
	public Fields getFields(Class<?> entityClass, Field parentField) {
		return MapperUtils.getFields(entityClass, parentField).entity()
				.accept((field) -> !isIgnore(entityClass, field.getGetter()));
	}
}
