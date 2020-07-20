package scw.value.property;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import scw.value.AnyValue;
import scw.value.Value;

public class StaticFieldPropertyFactory extends PropertyFactory {
	private final Class<?> clazz;
	private final String prefix;
	private final boolean toUpperCase;

	public StaticFieldPropertyFactory(Class<?> clazz, String prefix, boolean toUpperCase) {
		super(true, true);
		this.clazz = clazz;
		this.prefix = prefix;
		this.toUpperCase = toUpperCase;
	}

	public final Class<?> getClazz() {
		return clazz;
	}

	public final String getPrefix() {
		return prefix;
	}

	public final boolean isToUpperCase() {
		return toUpperCase;
	}

	protected Object getFieldValue(String prefix, String key)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		String k = key.toUpperCase();
		if (k.startsWith(prefix)) {
			k = k.substring(prefix.length());
			Field field = clazz.getDeclaredField(k);
			if (!Modifier.isStatic(field.getModifiers())) {
				return null;
			}
			return field.get(null);
		}
		return null;
	}

	@Override
	public Value get(String key) {
		try {
			Object value = isToUpperCase() ? getFieldValue(prefix.toUpperCase(), key.toUpperCase())
					: getFieldValue(prefix, key);
			if (value != null) {
				return new AnyValue(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.get(key);
	}
}
