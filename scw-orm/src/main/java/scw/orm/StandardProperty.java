package scw.orm;

import scw.mapper.Field;

public class StandardProperty extends StandardPropertyDescriptor implements
		Property {
	private Field field;

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}
