package scw.mapper;

import java.io.Serializable;

public class FieldContext implements Serializable {
	private static final long serialVersionUID = 1L;
	private final FieldContext parentContext;
	private final Field field;

	public FieldContext(FieldContext parentContext, Field field) {
		this.parentContext = parentContext;
		this.field = field;
	}

	public FieldContext getParentContext() {
		return parentContext;
	}

	public Field getField() {
		return field;
	}
}
