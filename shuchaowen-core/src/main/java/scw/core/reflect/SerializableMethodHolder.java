package scw.core.reflect;

import java.lang.reflect.Method;

public class SerializableMethodHolder extends AbstractSerializableMethodHolder {
	private static final long serialVersionUID = 1L;
	private Class<?> belongClass;
	private Class<?>[] parameterTypes;

	/**
	 * 用于序列化
	 */
	private SerializableMethodHolder() {
		super(null);
	};

	public SerializableMethodHolder(Class<?> belongClass, Method method) {
		super(method.getName());
		this.belongClass = belongClass;
		this.parameterTypes = method.getParameterTypes();
	}

	public Class<?> getBelongClass() {
		return belongClass;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
}
