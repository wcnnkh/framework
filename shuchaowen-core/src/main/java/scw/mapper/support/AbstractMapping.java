package scw.mapper.support;

import java.util.LinkedList;
import java.util.ListIterator;

import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ClassUtils;
import scw.mapper.FieldContext;
import scw.mapper.FieldDescriptor;
import scw.mapper.Mapper;
import scw.mapper.FilterFeature;
import scw.mapper.Mapping;

public abstract class AbstractMapping implements Mapping {

	public <T> T newInstance(Class<? extends T> type) {
		return InstanceUtils.INSTANCE_FACTORY.getInstance(type);
	}

	public Object mapping(Class<?> entityClass, FieldContext fieldContext,
			Mapper fieldFactory) throws Exception{
		if (isNesting(fieldContext)) {
			return fieldFactory.mapping(fieldContext.getField().getSetter()
					.getType(), fieldContext, this);
		} else {
			return getValue(fieldContext);
		}
	}
	
	public boolean accept(FieldContext fieldContext) {
		return FilterFeature.GETTER_IGNORE_STATIC.getFilter().accept(fieldContext);
	}

	protected boolean isNesting(FieldContext fieldContext) {
		Class<?> type = fieldContext.getField().getSetter().getType();
		return !(type == String.class || ClassUtils.isPrimitiveOrWrapper(type));
	}

	protected abstract Object getValue(FieldContext fieldContext);

	protected String getDisplayName(FieldDescriptor fieldMetadata) {
		return ParameterUtils.getDisplayName(fieldMetadata);
	}

	protected final String getNestingDisplayName(FieldContext fieldContext) {
		if (fieldContext.getParentContext() == null) {
			return getDisplayName(fieldContext.getField().getSetter());
		}

		LinkedList<FieldDescriptor> fieldMetadatas = new LinkedList<FieldDescriptor>();
		FieldContext parent = fieldContext;
		while (parent != null) {
			fieldMetadatas.add(parent.getField().getSetter());
			parent = parent.getParentContext();
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
