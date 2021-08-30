package io.basc.framework.value;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.json.JSONSupport;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

public class StringValue extends AbstractValue {
	private static final long serialVersionUID = 1L;
	private String value;
	private transient JSONSupport jsonSupport;

	public StringValue(String value) {
		this(value, null);
	}

	public StringValue(String value, @Nullable Value defaultValue) {
		super(defaultValue);
		this.value = value;
	}

	public JSONSupport getJsonSupport() {
		return jsonSupport == null ? JSONUtils.getJsonSupport() : jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public String getAsString() {
		return value;
	}

	public boolean isSupportArray() {
		return true;
	}

	public String[] split(String value) {
		return StringUtils.commonSplit(value);
	}

	protected Value parseValue(String text) {
		return new StringValue(text, getDefaultValue());
	}

	@SuppressWarnings("unchecked")
	public <E> E[] getAsArray(ResolvableType componentType) {
		String[] values = split(getAsString());
		if (values == null) {
			return null;
		}

		Object array = Array.newInstance(componentType.getRawClass(),
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
		return getAsArray(ResolvableType.forClass(componentType));
	}

	@Override
	protected Object getAsNonBaseType(ResolvableType type) {
		if (value == null) {
			return getDefaultValue().getAsObject(type);
		}
		
		if(type.isInstance(value)) {
			return value;
		}

		Class<?> rawClass = type.getRawClass();
		if (rawClass == Object.class || rawClass == null) {
			return value;
		}
		
		if (isSupportArray() && type.isArray()) {
			return getAsArray(type.getComponentType());
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
			return ObjectUtils.nullSafeEquals(value, ((StringValue) obj).value);
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
