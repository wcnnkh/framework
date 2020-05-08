package scw.mapper;

import java.lang.reflect.Method;

public class Field extends FieldMetadata implements java.lang.Cloneable {
	private static final long serialVersionUID = 1L;
	private final Field parentField;
	
	public Field(Field parentField, Class<?> declaringClass, String name, java.lang.reflect.Field field, Method getter, Method setter) {
		this(parentField, new DefaultGetter(declaringClass, name, field, getter),
				new DefaultSetter(declaringClass, name, field, setter));
	}

	public Field(Field parentField, Getter getter, Setter setter) {
		super(getter, setter);
		this.parentField = parentField;
	}
	
	public Field getParentField() {
		return parentField;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
