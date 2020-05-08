package scw.mapper.support;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;

import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ClassUtils;
import scw.mapper.Mapping;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.FilterFeature;
import scw.mapper.Mapper;

public abstract class AbstractMapping implements Mapping {

	public <T> T newInstance(Class<? extends T> type) {
		return InstanceUtils.INSTANCE_FACTORY.getInstance(type);
	}
	
	public <T> T mapping(Class<? extends T> entityClass, Collection<Field> fields, Mapper mapper) throws Exception {
		T entity = newInstance(entityClass);
		for(Field field : fields){
			Object value;
			if(isNesting(field.getSetter())){
				value = mapper.mapping(field.getSetter().getType(), field, this);
			}else{
				value = getValue(field);
			}
			
			if(value != null){
				field.getSetter().set(entity, value);
			}
		}
		return entity;
	}
	
	public boolean accept(Field field) {
		return FilterFeature.GETTER_IGNORE_STATIC.getFilter().accept(field);
	}

	protected boolean isNesting(FieldDescriptor fieldDescriptor) {
		Class<?> type = fieldDescriptor.getType();
		return !(type == String.class || ClassUtils.isPrimitiveOrWrapper(type));
	}

	protected abstract Object getValue(Field field);

	protected String getDisplayName(FieldDescriptor fieldMetadata) {
		return ParameterUtils.getDisplayName(fieldMetadata);
	}

	protected final String getNestingDisplayName(Field field) {
		if (field.getParentField() == null) {
			return getDisplayName(field.getSetter());
		}

		LinkedList<FieldDescriptor> fieldMetadatas = new LinkedList<FieldDescriptor>();
		Field parent = field;
		while (parent != null) {
			fieldMetadatas.add(parent.getSetter());
			parent = parent.getParentField();
		}

		StringBuilder sb = new StringBuilder();
		ListIterator<FieldDescriptor> iterator = fieldMetadatas
				.listIterator(fieldMetadatas.size());
		while (iterator.hasPrevious()) {
			FieldDescriptor fieldMetadata = iterator.next();
			sb.append(getDisplayName(fieldMetadata));
			if (iterator.hasPrevious()) {
				sb.append(".");
			}
		}
		return sb.toString();
	}
}
