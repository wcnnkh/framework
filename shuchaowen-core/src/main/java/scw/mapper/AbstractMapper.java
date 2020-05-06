package scw.mapper;

import java.util.LinkedList;
import java.util.ListIterator;

import scw.core.instance.InstanceUtils;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ClassUtils;

public abstract class AbstractMapper implements Mapper {
	private FieldFactory fieldFactory = MapperUtils.getFieldFactory();
	private FieldContextFilter fieldContextFilter;
	private FieldFilterType[] fieldFilterTypes = new FieldFilterType[] { FieldFilterType.GETTER_IGNORE_STATIC };

	public FieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public void setFieldFactory(FieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	public FieldContextFilter getFieldContextFilter() {
		return fieldContextFilter;
	}

	public void setFieldContextFilter(FieldContextFilter fieldContextFilter) {
		this.fieldContextFilter = fieldContextFilter;
	}

	public FieldFilterType[] getFieldFilterTypes() {
		return fieldFilterTypes;
	}

	public void setFieldFilterTypes(FieldFilterType[] fieldFilterTypes) {
		this.fieldFilterTypes = fieldFilterTypes;
	}

	protected <T> T newInstance(Class<? extends T> type) {
		return InstanceUtils.INSTANCE_FACTORY.getInstance(type);
	}

	public <T> T mapping(Class<? extends T> type, FieldContext parentContext) throws Exception {
		T instance = newInstance(type);
		for (FieldContext fieldContext : fieldFactory.getFieldContexts(type, parentContext, getFieldContextFilter(),
				getFieldFilterTypes())) {
			if (!fieldContext.getField().isSupportSetter()) {
				continue;
			}

			Object value;
			if (isNesting(fieldContext)) {
				value = mapping(fieldContext.getField().getSetter().getType(), fieldContext);
			} else {
				value = getValue(fieldContext);
			}
			
			if(value == null){
				continue;
			}
			fieldContext.getField().getSetter().set(instance, value);
		}
		return instance;
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
		ListIterator<FieldDescriptor> iterator = fieldMetadatas.listIterator(fieldMetadatas.size());
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
