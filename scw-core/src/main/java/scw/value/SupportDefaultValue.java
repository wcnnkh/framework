package scw.value;

import java.lang.reflect.Type;

public abstract class SupportDefaultValue extends AbstractValue {
	private final Value defaultValue;

	public SupportDefaultValue(Value defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Value getDefaultValue() {
		return defaultValue;
	}
	
	@Override
	public <T> T getAsObject(Class<T> type) {
		T v = super.getAsObject(type);
		return v == null ? getDefaultValue().getAsObject(type) : v;
	}

	@Override
	public Object getAsObject(Type type) {
		Object v = super.getAsObject(type);
		return v == null ? getDefaultValue().getAsObject(type) : v;
	}
}
