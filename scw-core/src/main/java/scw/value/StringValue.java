package scw.value;

import java.io.Serializable;
import java.lang.reflect.Array;

import scw.core.ResolvableType;
import scw.core.utils.StringUtils;
import scw.json.JSONSupport;
import scw.json.JSONUtils;

public class StringValue extends AbstractStringValue implements Serializable {
	private static final long serialVersionUID = 1L;
	private String value;
	private transient JSONSupport jsonSupport;

	public StringValue(String value) {
		this(value, EmptyValue.INSTANCE);
	}

	public StringValue(String value, Value defaultValue) {
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

		Object array = Array.newInstance(componentType.getRawClass(), values.length);
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
	public Object getAsObject(ResolvableType resolvableType) {
		if(isSupportArray() && resolvableType.isArray()){
			return getAsArray(resolvableType.getComponentType());
		}
		return super.getAsObject(resolvableType);
	}
	
	@Override
	protected Object getAsObjectNotSupport(ResolvableType type,
			Class<?> rawClass) {
		return getJsonSupport().parseObject(getAsString(), type.getType());
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(value);
	}
}
