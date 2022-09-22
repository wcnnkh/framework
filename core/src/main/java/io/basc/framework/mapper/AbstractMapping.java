package io.basc.framework.mapper;

import java.util.LinkedList;
import java.util.ListIterator;

import io.basc.framework.env.Sys;
import io.basc.framework.util.ClassUtils;

public abstract class AbstractMapping implements Mapping {

	public <T> T newInstance(Class<? extends T> type) {
		return Sys.getEnv().getInstance(type);
	}

	public <T> T mapping(Class<T> entityClass, Fields fields) {
		T entity = newInstance(entityClass);
		for (Field field : fields) {
			Object value;
			if (isNesting(field.getSetter())) {
				value = mapping(field.getSetter().getType(), Fields.getFields(field.getSetter().getType(), field));
			} else {
				value = getValue(field);
			}

			if (value != null) {
				field.getSetter().set(entity, value);
			}
		}
		return entity;
	}

	public boolean accept(Field field) {
		return FieldFeature.IGNORE_STATIC.accept(field);
	}

	protected boolean isNesting(FieldDescriptor fieldDescriptor) {
		Class<?> type = fieldDescriptor.getType();
		return !(type == String.class || ClassUtils.isPrimitiveOrWrapper(type));
	}

	protected abstract Object getValue(Field field);

	protected final String getNestingDisplayName(Field field) {
		if (field.getParent() == null) {
			return field.getSetter().getName();
		}

		LinkedList<FieldDescriptor> fieldMetadatas = new LinkedList<FieldDescriptor>();
		Field parent = field;
		while (parent != null) {
			fieldMetadatas.add(parent.getSetter());
			parent = parent.getParent();
		}

		StringBuilder sb = new StringBuilder();
		ListIterator<FieldDescriptor> iterator = fieldMetadatas.listIterator(fieldMetadatas.size());
		while (iterator.hasPrevious()) {
			FieldDescriptor fieldMetadata = iterator.next();
			sb.append(fieldMetadata.getName());
			if (iterator.hasPrevious()) {
				sb.append(".");
			}
		}
		return sb.toString();
	}
}
