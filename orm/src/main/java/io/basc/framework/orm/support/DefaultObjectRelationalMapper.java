package io.basc.framework.orm.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFactory;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapFields;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.annotation.AnnotationObjectRelationalResolverExtend;

public class DefaultObjectRelationalMapper extends DefaultObjectRelationalResolver implements ObjectRelationalMapper {
	private FieldFactory fieldFactory;

	public DefaultObjectRelationalMapper() {
		addService(new AnnotationObjectRelationalResolverExtend());
	}

	public FieldFactory getFieldFactory() {
		return fieldFactory == null ? MapperUtils.getFieldFactory() : fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	@Override
	public final Fields getFields(Class<?> entityClass) {
		return ObjectRelationalMapper.super.getFields(entityClass);
	}

	@Override
	public Fields getFields(Class<?> entityClass, Field parentField) {
		Fields fields = getFieldFactory().getFields(entityClass, parentField).accept(FieldFeature.IGNORE_STATIC)
				.accept((f) -> f.isSupportGetter() || f.isSupportSetter());
		return new MapFields(fields, (stream) -> stream.map((field) -> new Field(field.getParentField(),
				!field.isSupportGetter() || isIgnore(entityClass, field.getGetter()) ? null : field.getGetter(),
				!field.isSupportSetter() || isIgnore(entityClass, field.getSetter()) ? null : field.getSetter())));
	}
}
