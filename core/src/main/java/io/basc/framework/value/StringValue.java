package io.basc.framework.value;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.JSONSupport;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class StringValue extends AbstractValue implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;
	private transient JSONSupport jsonSupport;
	
	public StringValue(String value) {
		this.value = value;
	}

	public JSONSupport getJsonSupport() {
		return jsonSupport == null ? JSONUtils.getJsonSupport() : jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}
	
	@Override
	public Object getSourceValue() {
		return value;
	}

	public String getAsString() {
		return value;
	}

	public boolean isSupportArray() {
		return true;
	}

	public String[] split(String value) {
		return StringUtils.splitToArray(value);
	}

	protected Value parseValue(String text) {
		return new StringValue(text);
	}

	@SuppressWarnings("unchecked")
	public <E> E[] getAsArray(TypeDescriptor componentType) {
		String[] values = split(getAsString());
		if (values == null) {
			return null;
		}

		Object array = Array.newInstance(componentType.getType(),
				values.length);
		for (int i = 0; i < values.length; i++) {
			Value value = parseValue(values[i]);
			if (value != null) {
				Array.set(array, i, value.getAsObject(componentType));
			}
		}
		return (E[]) array;
	}

	public <E> E[] getAsArray(Class<E> componentType) {
		return getAsArray(TypeDescriptor.valueOf(componentType));
	}

	@Override
	protected Object getAsNonBaseType(TypeDescriptor type) {
		if (value == null) {
			return null;
		}
		
		if(type.getType().isInstance(value)) {
			return value;
		}

		Class<?> rawClass = type.getType();
		if (rawClass == Object.class || rawClass == null) {
			return value;
		}
		
		if (isSupportArray() && type.isArray()) {
			return getAsArray(type.getElementTypeDescriptor());
		}
		return getJsonSupport().parseObject(getAsString(), type.getType());
	}

	@Override
	public int hashCode() {
		return value == null ? super.hashCode() : value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (value == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof StringValue) {
			return ObjectUtils.equals(value, ((StringValue) obj).value);
		}
		return false;
	}

	public static <T> T parse(String text, Class<T> type) {
		return new StringValue(text).getAsObject(type);
	}

	public static Object parse(String text, Type type) {
		return new StringValue(text).getAsObject(type);
	}
}
