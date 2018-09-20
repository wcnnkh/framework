package shuchaowen.core.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BeanConstructor {
	private Class<?> type;
	private Class<?>[] types;
	private Object[] args;
	
	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public Class<?>[] getTypes() {
		return types;
	}

	public void setTypes(Class<?>[] types) {
		this.types = types;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Object newInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		Constructor<?> constructor = type.getConstructor(types);
		return constructor.newInstance(args);
	}
}
