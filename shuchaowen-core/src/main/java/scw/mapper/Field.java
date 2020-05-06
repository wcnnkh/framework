package scw.mapper;

import java.io.Serializable;
import java.lang.reflect.Method;

public class Field implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Getter getter;
	private final Setter setter;
	
	public Field(Class<?> declaringClass, String name, java.lang.reflect.Field field, Method getter, Method setter) {
		this(new DefaultGetter(declaringClass, name, field, getter),
				new DefaultSetter(declaringClass, name, field, setter));
	}

	public Field(Getter getter, Setter setter) {
		this.getter = getter;
		this.setter = setter;
	}

	public Getter getGetter() {
		return getter;
	}

	public Setter getSetter() {
		return setter;
	}

	public boolean isSupportGetter() {
		return getter != null;
	}

	public boolean isSupportSetter() {
		return setter != null;
	}
}
