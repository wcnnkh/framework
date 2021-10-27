package io.basc.framework.orm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFactory;
import io.basc.framework.mapper.Fields;
import io.basc.framework.mapper.MapperUtils;

public class DefaultObjectRelationalMapping extends DefaultObjectRelationalResolver implements ObjectRelationalMapping {
	private FieldFactory fieldFactory;

	public FieldFactory getFieldFactory() {
		return fieldFactory == null ? MapperUtils.getFieldFactory() : fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	@Override
	public Property resolve(Class<?> entityClass, Field field) {
		return new DefaultProperty(entityClass, field, this);
	}

	@Override
	public EntityStructure<? extends Property> resolve(Class<?> entityClass) {
		return new DefaultEntityStructure(entityClass);
	}

	@Override
	public Fields getFields(Class<?> entityClass, Field parentField) {
		return MapperUtils.getFields(entityClass, parentField).entity();
	}

	private class DefaultEntityStructure implements EntityStructure<Property> {
		private final Class<?> entityClass;

		public DefaultEntityStructure(Class<?> entityClass) {
			this.entityClass = entityClass;
		}

		@Override
		public Class<?> getEntityClass() {
			return entityClass;
		}

		@Override
		public String getName() {
			return DefaultObjectRelationalMapping.super.getName(entityClass);
		}

		@Override
		public List<Property> getRows() {
			return stream().collect(Collectors.toList());
		}

		@Override
		public Stream<Property> stream() {
			return resolve(entityClass, getFields(entityClass).all()).map((p) -> p);
		}

	}
}
